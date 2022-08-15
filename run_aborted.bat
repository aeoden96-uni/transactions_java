start "NameServer" "run_nameserver.bat"

start "Master" "run_master.bat" -num_of_transactions 4  -id 0

timeout /t 2
start "Slave1" "run_slave.bat" -num_of_transactions 4 -id 1 -propose t
timeout /t 2
start "Slave2" "run_slave.bat" -num_of_transactions 4 -id 2 -propose f
timeout /t 2
start "Slave3" "run_slave.bat" -num_of_transactions 4 -id 3 -propose t