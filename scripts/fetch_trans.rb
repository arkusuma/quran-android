#!/usr/bin/env ruby

require 'open-uri'
require 'nokogiri'
require 'sqlite3'
require 'uri'

def convert(url, meta)
  puts 'Downloading ' + meta['id'] + '...'
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
    if line.include? '11|82|82|'
      puts 'Fixing:'
      puts line
      line.sub! '82|82', '82'
      puts line
    elsif line.include? '要|见'
      puts 'Fixing:'
      puts line
      line.sub! '要|见', '要见'
      puts line
    elsif line.include? '"|'
      puts 'Fixing:'
      puts line
      line.sub! '"|', '"'
      puts line
    elsif line.include? ' | '
      puts 'Fixing:'
      puts line
      line.sub! ' | ', ' '
      puts line
    end
    cols = line.split('|')
    print line if cols.length > 3
    next if cols.length != 3
    db.execute 'INSERT INTO quran VALUES (?,?,?)', cols[0], cols[1], cols[2]
    count += 1
  end

  db.execute 'COMMIT TRANSACTION'
  db.close

  if count != 6236
    File.delete target
    puts "Invalid aya count: #{count}"
  end
end

doc = Nokogiri::HTML(open('http://tanzil.net/trans/'))
doc.css('.transList tbody tr').each do |row|
  cols = row.css('td')
  lang = cols[0].text
  name = cols[1].text
  bio = cols[2].at_css('.bio')
  if bio
    bio.remove
    bio = bio['href'].sub('http://tanzil.net/pub/url/?q=', '')
    bio = 'http://' + URI.unescape(bio)
  end
  translator = cols[2].text.strip
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
  meta['biography'] = bio if bio

  convert(download_url, meta)
end
