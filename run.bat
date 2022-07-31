start "NameServer" "run_nameserver.bat"

start "Master" "run_master.bat"

timeout /t 2
start "Slave1" "run_slave.bat"
