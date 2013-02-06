#!/usr/bin/env ruby

require 'sqlite3'

if ARGV.length != 1
  puts 'Usage: convert <source_text>'
  exit
end

source = ARGV[0]
target = source.gsub(/\.[^.]*$/, '')

def xform(s, type)
  s = String.new(s)
  if type == 5
    s.gsub!("\u066E", "\u0649") # DOTLESS BEH => ALEF MAKSURA
    s.gsub!("\u064E\u06E2", "\u064B\u06E2") # FATHA + SMALL MEEM => FATHATAN + SMALL MEEM
    s.gsub!("\u064F\u06E2", "\u064C\u06E2") # DAMMAH + SMALL MEEM => DAMMATAN + SMALL MEEM
    s.gsub!("\u0650\u06ED", "\u064D\u06ED") # KASRA + SMALL LOW MEEM => KASRATAN + SMALL LOW MEEM
  end
  s.gsub!(/[\u0640\u06DF]/, '') # TATWEEL | SMALL HIGH ROUNDED ZERO
  s.gsub!(/\u064E([\u0648\u0649]?)[\u0670\u0672]/, "\u0670\\1") # FATHAH + (SS ALEF | ALEF WAVY HAMZA)
  s.gsub!("\u0671", "\u0627") # ALEF WASLA => ALEF
  s.gsub!("\u0627\u0652", "\u0627") # ALEF + SUKUN => ALEF
  return s
end

first = true
type = nil

File.delete target if File.exists? target
db = SQLite3::Database.new target
db.execute 'BEGIN TRANSACTION'
bismillah = nil
File.readlines(source).each do |line|
  line.strip!
  f = line.split '|'
  if f.length == 3
    if first
      first = false
      db.execute 'CREATE TABLE quran (
          sura INT1, aya INT2, text TEXT, 
          UNIQUE(sura, aya) ON CONFLICT REPLACE)'
    end
    type = f.length
    sura, aya, text = f
    if aya == '1'
      if sura == '1'
        bismillah = text
      else
        text.gsub!(bismillah, '')
        text.strip!
      end
    end
    db.execute 'INSERT INTO quran VALUES (?,?,?)', sura, aya, xform(text, type)
  elsif f.length == 5
    if first
      first = false
      db.execute 'CREATE TABLE quran (
          sura INT1, aya INT2, word INT1, ar TEXT, tr TEXT,
          UNIQUE(sura, aya, word) ON CONFLICT REPLACE)'
    end
    type = f.length
    sura, aya, word, ar, tr = f
    db.execute 'INSERT INTO quran VALUES (?,?,?,?,?)', sura, aya, word, xform(ar, type), tr
  end
end
db.execute 'COMMIT TRANSACTION'
db.close

`gzip -9 -f -S .png #{target}`
if type == 5
  `split -n2 #{target}.png`
  `mv xaa #{target}1.png`
  `mv xab #{target}2.png`
  `rm #{target}.png`
end
