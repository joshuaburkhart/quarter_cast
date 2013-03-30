#!/usr/bin/ruby

#Usage: yahoo_fin_data_parser.rb <csv from finance.yahoo.com>

#Example: yahoo_fin_data_parser.rb CBST.csv

#NOTE: This program produces files containing quarterly movements.

INVALID = "<invalid>"
DECEMBER = "12"
SEPTEMBER = "09"
JUNE = "06"
MARCH = "03"
UP = "1"
DOWN = "0"
Q1_AFFIX = "_Q1_"
Q2_AFFIX = "_Q2_"
Q3_AFFIX = "_Q3_"
Q4_AFFIX = "_Q4_"
Q_DATA_LENGTH = 128

q1_start = INVALID
q2_start = INVALID
q3_start = INVALID
q4_start = INVALID
q1_finsh = INVALID
q2_finsh = INVALID
q3_finsh = INVALID
q4_finsh = INVALID

next_month = nil
crnt_month = nil
next_q_val = nil
next_q_mov = nil
next_d_price = nil
crnt_d_price = nil
crnt_data_line = nil

lagging_data_line = nil

csv_file = ARGV[0]
data_filehandl = File.open(csv_file,"r")
if(csv_file.match(/([A-Z]+.csv)/))
    csv_filename = $1
    puts "Processing #{csv_filename}..."
else
    puts "Error processing #{csv_file}"
    puts "Aborting..."
    exit
end

while(data_line = data_filehandl.gets)
    puts "Processing dataline: #{data_line}"
    if(data_line.match(/^[0-9]{4}-([0-9]{2})-[0-9]{2},.+,.+,.+,.+,.+,.+$/))
        if(next_month.nil?)
            next_month = $1
        end
        crnt_month = $1
        if(crnt_month != next_month)
            case crnt_month
            when DECEMBER
                lagging_data_line.match(/^[0-9]{4}-([0-9]{2}-[0-9]{2}),.+,.+,.+,.+,.+,.+$/)
                q2_start = $1
                data_line.match(/^[0-9]{4}-([0-9]{2}-[0-9]{2}),.+,.+,.+,.+,.+,.+$/)
                q1_finsh = $1
            when SEPTEMBER
                lagging_data_line.match(/^[0-9]{4}-([0-9]{2}-[0-9]{2}),.+,.+,.+,.+,.+,.+$/)
                q1_start = $1
                data_line.match(/^[0-9]{4}-([0-9]{2}-[0-9]{2}),.+,.+,.+,.+,.+,.+$/)
                q4_finsh = $1
            when JUNE
                lagging_data_line.match(/^[0-9]{4}-([0-9]{2}-[0-9]{2}),.+,.+,.+,.+,.+,.+$/)
                q4_start = $1
                data_line.match(/^[0-9]{4}-([0-9]{2}-[0-9]{2}),.+,.+,.+,.+,.+,.+$/)
                q3_finsh = $1
            when MARCH
                lagging_data_line.match(/^[0-9]{4}-([0-9]{2}-[0-9]{2}),.+,.+,.+,.+,.+,.+$/)
                q3_start = $1
                data_line.match(/^[0-9]{4}-([0-9]{2}-[0-9]{2}),.+,.+,.+,.+,.+,.+$/)
                q2_finsh = $1
            end
        end
        next_month = crnt_month
        if(!lagging_data_line.nil?)
            if(next_q_val.nil? && lagging_data_line.match(/^[0-9]{4}-(#{q1_finsh}|#{q2_finsh}|#{q3_finsh}|#{q4_finsh}).+$/))
                lagging_data_line.match(/^[0-9]{4}-[0-9]{2}-[0-9]{2},.+,.+,.+,.+,.+,(.+)$/)
                next_q_val = Float($1)
            elsif(next_q_mov.nil? && lagging_data_line.match(/^[0-9]{4}-(#{q1_finsh}|#{q2_finsh}|#{q3_finsh}|#{q4_finsh}).+$/))
                lagging_data_line.match(/^[0-9]{4}-[0-9]{2}-[0-9]{2},.+,.+,.+,.+,.+,(.+)$/)
                crnt_q_val = Float($1)
                next_q_mov = crnt_q_val > next_q_val ? DOWN : UP
                crnt_data_line = next_q_mov
                next_d_price = crnt_q_val
                next_q_val = crnt_q_val
            elsif(!crnt_data_line.nil? && lagging_data_line.match(/^([0-9]{4})-(#{q1_start}|#{q2_start}|#{q3_start}|#{q4_start}).+$/))
                crnt_data_line << ",#{DOWN}"
                #we assume more recent days have more influence and want a uniform number of features
                while(crnt_data_line.length < Q_DATA_LENGTH)
                    crnt_data_line << ",#{DOWN}"
                end
                crnt_data_line.reverse!
                crnt_filename = $1
                case $2
                when q1_start
                    crnt_filename << Q4_AFFIX
                when q2_start
                    crnt_filename << Q1_AFFIX
                when q3_start
                    crnt_filename << Q2_AFFIX
                when q4_start
                    crnt_filename << Q3_AFFIX
                else
                    puts "ERROR: UNRECOGNIZED QUARTER"
                    puts "#{lagging_data_line}"
                    puts "q1_start: #{q1_start}"
                    puts "q2_start: #{q2_start}"
                    puts "q3_start: #{q3_start}"
                    puts "q4_start: #{q4_start}"
                    puts "q1_finsh: #{q1_finsh}"
                    puts "q2_finsh: #{q2_finsh}"
                    puts "q3_finsh: #{q3_finsh}"
                    puts "q4_finsh: #{q4_finsh}"
                    puts "crnt_data_line: #{crnt_data_line}"
                    puts "crnt_month: #{crnt_month}"
                    puts "next_month: #{next_month}"
                    exit
                end
                crnt_filename << csv_filename
                crnt_filehandl = File.open(crnt_filename,"w")
                crnt_filehandl.puts(crnt_data_line)
                crnt_filehandl.close
                next_q_mov = nil
            elsif(!next_d_price.nil? && lagging_data_line.match(/^[0-9]{4}-[0-9]{2}-[0-9]{2},.+,.+,.+,.+,.+,(.+)$/))
                crnt_d_price = Float($1)
                crnt_d_mov = crnt_d_price > next_d_price ? DOWN : UP
                crnt_data_line << ",#{crnt_d_mov}"
                next_d_price = crnt_d_price
            end
        end
        lagging_data_line = data_line
    end
end

data_filehandl.close
