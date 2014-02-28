#!/usr/bin/env ruby

require 'open-uri'
require 'nokogiri'
require 'sqlite3'
require 'json'
require 'uri'

def fix(line)
  orig = line.dup
  if line.include? '11|82|82|'
    line.sub! '82|82', '82'
  elsif line.include? '要|见'
    line.sub! '要|见', '要见'
  elsif line.include? '"|'
    line.sub! '"|', '"'
  elsif line.include? ' | '
    line.sub! ' | ', ' '
  end
  if orig != line
    puts "Fixing:"
    puts orig
    puts line
  end
  return line
end

def convert(url, meta)
  puts 'Downloading: ' + meta['id']
  source = open(url).read
  
  target = meta['id']
  File.delete target if File.exists? target

  db = SQLite3::Database.new target
  db.execute 'BEGIN TRANSACTION'

  db.execute 'CREATE TABLE meta (
      key TEXT, val TEXT,
      UNIQUE(key) ON CONFLICT REPLACE)'
  db.execute 'CREATE TABLE quran (
      sura INT1, aya INT2, text TEXT, 
      UNIQUE(sura, aya) ON CONFLICT REPLACE)'
  
  meta.each {|key, val| db.execute 'INSERT INTO meta VALUES (?,?)', key, val }

  count = 0
  source.each_line do |line|
    line = fix(line)
    cols = line.split('|')
    print line if cols.length > 3
    next if cols.length != 3
    db.execute 'INSERT INTO quran VALUES (?,?,?)', cols[0], cols[1], cols[2]
    count += 1
  end

  db.execute 'COMMIT TRANSACTION'
  db.close

  if count == 6236
    `gzip -9 -f #{target}`
  else
    File.delete target
    puts "Invalid aya count: #{count}"
  end
end

doc = Nokogiri::HTML(open('http://tanzil.net/trans/'))
all = []
doc.css('.transList tbody tr').each do |row|
  cols = row.css('td')
  lang = cols[0].text.strip
  name = cols[1].text.strip
  bios = []
  cols[2].css('.bio').each do |bio|
    bio.remove
    bio = bio['href'].sub('http://tanzil.net/pub/url/?q=', '')
    bio = 'http://' + URI.unescape(bio)
    bios.push(bio)
  end
  translator = cols[2].text.gsub("\u00a0", ' ').strip
  download_url = cols[3].at_css('.download')['href']
  diff_url = cols[3].at_css('.diff')['href']
  diff = Nokogiri::HTML(open(diff_url))
  modified = diff.at_css('.date').inner_html.gsub(/.*>/, '').strip
  id = download_url.gsub(/.*\//, '')

  meta = {
    'id' => id,
    'language' => lang,
    'name' => name,
    'translator' => translator,
    'modified' => modified }
  meta['biography'] = bios.join ' ' if bios.length > 0
  all.push(meta)

  convert(download_url, meta)
end

File.open('translations.json', 'w') {|f| f.write JSON.pretty_generate all }
