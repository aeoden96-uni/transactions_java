# Transactions in java

Colaborators:
* Ivona Čižić
* Mateo Martinjak

> Run project by running one of the following:
1. ```run_OK.bat``` (console; compile needed)
2. ```run_aborted.bat``` (console; compile needed)
3. ```java -jar .\transactions_java.jar``` (get the executable from [here](https://github.com/aeoden96-uni/transactions_java/releases/tag/0.5))

Description:
```
Transakcija je niz operacija nad podacima koje se ponašaju kao jedna nedjeljiva cjelina.
Istovremeno izvođenje više transakcija mora biti ekvivalentno nekom njihovom
sekvencijalnom izvođenju. Ako transakcija zbog greške ne dođe do kraja, tada se njezin
dotadašnji učinak mora neutralizirati (rollback). Ako je transakcija došla do kraja, tada se
njezin ukupni učinak mora trajno pohraniti (commit). U projektu treba obraditi problem
kontrole konkurentnog izvođenja transakcija, kao i tehnike za neutralizaciju transakcije u
slučaju kad je došlo do greške prije kraja njezinog izvođenja. No najveću pažnju u projektu
treba posvetiti problematici distribuiranog izvršavanja transakcije. Dakle ako se transakcija
sastoji od dijelova koji su raspoređeni na više procesa (računala), kako osigurati da svi ti
dijelovi složno naprave ili commit ili rollback? Riječ je o specifičnom problemu usuglašavanja.
U projektu treba objasniti, analizirati, implementirati i testirati poznati algoritam dvofaznog
pohranjivanja (two-phase commit). 
```

Literature:

Garg V.K. [Concurrent and Distributed Computing in Java](http://users.ece.utexas.edu/~garg/jbk.html) Wiley – IEEE Press CHAPTER 16

Robert Manger [Distribuirani procesi](http://web.studenti.math.hr/~manger/protect/DP-Skripta.pdf)

