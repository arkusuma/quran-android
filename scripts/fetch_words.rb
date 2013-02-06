#!/usr/bin/env ruby

require 'open-uri'
require 'json'

ayas = [ 0,7,286,200,176,120,165,206,75,129,109,123,111,43,52,99,128,
  111,110,98,135,112,78,118,64,77,227,93,88,69,60,34,30,73,54,45,83,
  182,88,75,85,54,53,89,59,37,35,38,29,18,45,60,49,62,55,78,96,29,22,
  24,13,14,11,11,18,12,12,30,52,52,44,28,28,20,56,40,31,50,40,46,42,
  29,19,36,25,22,17,19,26,30,20,15,21,11,8,8,19,5,8,8,11,11,8,3,9,5,
  4,7,3,6,3,5,4,5,6 ]

for sura in 1..114
  url = "http://www.allahsquran.com/learn/ayas-s#{sura}d7q1f0o#{ayas[sura]}.js"
  json = open(url).read
  result = JSON.parse(json)
  i = 1
  result.each do |aya|
    j = 1
    aya['w'].each do |line|
      line.each do |word|
        puts "#{sura}|#{i}|#{j}|#{word[0]}|#{word[1]}"
        j = j + 1
      end
    end
    i = i + 1
  end
end
