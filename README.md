# hw1

src src/main/java/com/github/mikhailerofeev/nlp/hw1

unit tests (примеры текстов, которые успешно "берёт" парсер): src/test/java/com/github/mikhailerofeev/nlp/hw1

opcorpora results: F1=0.985, precision=0.988, recall=0.982, accuracy=0.985, tp=8205955, fp=96511, fn=145627

hw01_data results: F1=0.980, precision=0.975, recall=0.986, accuracy=0.980, tp=85468, fp=2172, fn=1170

Очень много проблем из-за плохой рамзетки, на больших данных стал понятно даже, что нельзя ориентироваться на кавычки -- отключил эту фичу. Не понятно как быть с большой буквой после аббр.


# hw2

На моём сете (4x10=40 документов) Хи-квадрат выдал всего 3 аттрибута, что недостаточно.  Попытка классификации на всех аттрибутах выдала всего 20% точность в SMO. Другие смотреть не стал, стал улучшать фичи.

Решил побить тексты по предложениям (вот и парсер мой чудесный пригодился :)), получил 130 предложений, выборка аттрибутов для которых дала уже пордяка 10 аттрибутов. Нормализовал слова, получил ~40 аттрибутов. Сформировал тексты только из этих аттрибутов:
```
politics,москв кандидат выбор праймериз москв выбор кандидат един праймериз выбор москв депутат выбор  кандидат депутат    праймериз депутат кандидат депутат  выбор праймериз праймериз выбор депутат един  кандидат праймериз москв  москв депутат москв выбор  москв един   един праймериз кандидат выбор

economics,милл украин украин российск газ российск милл украин милл российск газ украин милл российск газ украин

culture,музе конкурс произведен  музе музе  конкурс искус традиц произведен художник традиц  конкурс традиц искус конкурс традиц искус  музе произведен искус произведен
```
Так себе, ну да ладно. Наверно зажгла бы уникализация, другие методы поиска аттрибутов. Датасет плохой -- экономику и политику сейчас не различить, "общество" ну слишком разношёрстная тема.

### Результаты на моём сете 

(Test mode:10-fold cross-validation)

weka.classifiers.functions.SMO -C 1.0 -L 0.001 -P 1.0E-12 -N 0 -V -1 -W 1 -K "weka.classifiers.functions.supportVector.PolyKernel -C 250007 -E 1.0"

```
Correctly Classified Instances          15               38.4615 %
Incorrectly Classified Instances        24               61.5385 %
Kappa statistic                          0.3188
Mean absolute error                      0.1699
Root mean squared error                  0.2896
Relative absolute error                 93.0573 %
Root relative squared error             95.1009 %
Coverage of cases (0.95 level)          92.3077 %
Mean rel. region size (0.95 level)      80.5128 %
Total Number of Instances               39     

=== Confusion Matrix ===

 a b c d e f g h i j   <-- classified as
 1 0 0 0 0 0 0 0 0 3 | a = politics
 0 2 1 0 0 0 0 0 0 1 | b = accidents
 0 0 0 0 0 0 0 0 0 3 | c = society
 0 1 0 3 0 0 0 0 0 0 | d = auto
 0 0 0 0 4 0 0 0 0 0 | e = sport
 0 0 2 2 0 0 0 0 0 0 | f = economics
 0 0 0 3 0 0 1 0 0 0 | g = hi-tech
 0 0 2 0 0 0 0 2 0 0 | h = internet
 0 0 1 0 0 0 0 0 1 2 | i = culture
 0 0 3 0 0 0 0 0 0 1 | j = science
 ```
 
 weka.classifiers.trees.RandomForest -I 10 -K 0 -S 1 -num-slots 1
 ```
 Correctly Classified Instances          17               43.5897 %
 Incorrectly Classified Instances        22               56.4103 %
 Kappa statistic                          0.3733
 Mean absolute error                      0.1247
 Root mean squared error                  0.2671
 Relative absolute error                 68.2968 %
 Root relative squared error             87.7221 %
 Coverage of cases (0.95 level)          71.7949 %
 Mean rel. region size (0.95 level)      31.0256 %
 Total Number of Instances               39     
   
 === Confusion Matrix ===
 
  a b c d e f g h i j   <-- classified as
  1 1 1 0 0 1 0 0 0 0 | a = politics
  2 2 0 0 0 0 0 0 0 0 | b = accidents
  0 1 1 0 0 0 0 0 0 1 | c = society
  0 1 0 3 0 0 0 0 0 0 | d = auto
  0 0 0 0 4 0 0 0 0 0 | e = sport
  0 0 1 1 0 1 0 0 0 1 | f = economics
  0 0 0 2 0 0 2 0 0 0 | g = hi-tech
  0 0 0 1 0 1 1 0 0 1 | h = internet
  0 0 1 0 0 0 0 0 2 1 | i = culture
  2 0 0 0 0 1 0 0 0 1 | j = science
  
  ```
 weka.classifiers.functions.Logistic -R 1.0E-8 -M -1
  
  
  ```
  Correctly Classified Instances          28               71.7949 %
  Incorrectly Classified Instances        11               28.2051 %
  Kappa statistic                          0.6862
  Mean absolute error                      0.0563
  Root mean squared error                  0.2063
  Relative absolute error                 30.8609 %
  Root relative squared error             67.7366 %
  Coverage of cases (0.95 level)          74.359  %
  Mean rel. region size (0.95 level)      13.5897 %
  Total Number of Instances               39     
  
 
   a b c d e f g h i j   <-- classified as
   3 1 0 0 0 0 0 0 0 0 | a = politics
   1 3 0 0 0 0 0 0 0 0 | b = accidents
   1 1 0 0 0 1 0 0 0 0 | c = society
   0 1 0 3 0 0 0 0 0 0 | d = auto
   0 0 0 0 4 0 0 0 0 0 | e = sport
   0 0 0 0 0 3 0 0 0 1 | f = economics
   0 0 0 1 0 0 2 0 1 0 | g = hi-tech
   0 0 0 0 0 0 0 4 0 0 | h = internet
   0 0 0 0 0 0 0 0 4 0 | i = culture
   0 0 2 0 0 0 0 0 0 2 | j = science
   
   ```
Закономерный **вывод**: когда много фичей и мало данных -- рулят простые методы.


Дальше подключил тексты других команд (275 в сумме), так же побил на предложения (2866), аттрибуты (242), и пересобрал тексты.

### Результаты на общем сете

SMO

```
=== Summary ===

Correctly Classified Instances         189               69.2308 %
Incorrectly Classified Instances        84               30.7692 %
Kappa statistic                          0.6573
Mean absolute error                      0.1633
Root mean squared error                  0.2778
Relative absolute error                 90.9331 %
Root relative squared error             92.706  %
Coverage of cases (0.95 level)          99.2674 %
Mean rel. region size (0.95 level)      80      %
Total Number of Instances              273     

=== Confusion Matrix ===

  a  b  c  d  e  f  g  h  i  j   <-- classified as
 21  0  4  0  0  3  0  0  0  0 |  a = politics
  1 26  4  0  0  0  1  0  0  0 |  b = accidents
  2  2 15  0  0  4  1  1  0  1 |  c = society
  0  1  2 12  0  0  0  1  0  0 |  d = auto
  1  0  2  0 29  0  0  0  0  0 |  e = sport
  4  0  5  1  0 11  2  3  0  0 |  f = economics
  2  0  2  1  0  2 12  5  0  2 |  g = hi-tech
  3  0  3  2  0  2  4 12  0  0 |  h = internet
  0  0  1  0  1  0  2  0 27  0 |  i = culture
  1  1  2  0  0  0  2  0  0 24 |  j = science
```
RandomForest

```
=== Summary ===

Correctly Classified Instances         164               60.0733 %
Incorrectly Classified Instances       109               39.9267 %
Kappa statistic                          0.5555
Mean absolute error                      0.1089
Root mean squared error                  0.2305
Relative absolute error                 60.6372 %
Root relative squared error             76.9292 %
Coverage of cases (0.95 level)          93.4066 %
Mean rel. region size (0.95 level)      37.8388 %
Total Number of Instances              273     

=== Confusion Matrix ===

  a  b  c  d  e  f  g  h  i  j   <-- classified as
 19  1  4  1  0  2  0  1  0  0 |  a = politics
  0 24  6  0  0  0  2  0  0  0 |  b = accidents
  3  4 12  0  1  2  1  1  0  2 |  c = society
  0  2  0 11  0  1  0  1  0  1 |  d = auto
  1  0  3  0 26  1  1  0  0  0 |  e = sport
  5  1  7  1  0  7  0  1  1  3 |  f = economics
  1  0  3  3  0  1 10  5  0  3 |  g = hi-tech
  1  0  3  2  0  2  7 11  0  0 |  h = internet
  0  0  1  1  2  2  1  0 24  0 |  i = culture
  2  1  4  0  0  2  1  0  0 20 |  j = science
```

Logistic regression

```
=== Summary ===

Correctly Classified Instances         205               ** 75.0916 % **
Incorrectly Classified Instances        68               24.9084 %
Kappa statistic                          0.7225
Mean absolute error                      0.0503
Root mean squared error                  0.2174
Relative absolute error                 28.0285 %
Root relative squared error             72.5435 %
Coverage of cases (0.95 level)          77.2894 %
Mean rel. region size (0.95 level)      10.9524 %
Total Number of Instances              273     

=== Confusion Matrix ===

  a  b  c  d  e  f  g  h  i  j   <-- classified as
 20  0  3  0  0  4  0  1  0  0 |  a = politics
  0 25  6  0  0  1  0  0  0  0 |  b = accidents
  3  3 13  0  0  2  3  0  2  0 |  c = society
  0  2  0 13  0  1  0  0  0  0 |  d = auto
  0  0  1  0 31  0  0  0  0  0 |  e = sport
  3  0  3  1  0 16  1  1  1  0 |  f = economics
  1  0  3  0  0  0 14  6  2  0 |  g = hi-tech
  0  0  1  2  0  1  4 18  0  0 |  h = internet
  0  0  0  0  0  0  0  0 31  0 |  i = culture
  0  0  1  0  0  0  3  2  0 24 |  j = science

```

Безобразные данные, слово "экономическ" зарулило на политике, а "крым" на экономике. Пробовал собирать аттрибуты более традиционным образом, пробовал брать меньше -- проседал.

**Вывод**: логическая регрессия уверенно зарулила на маленьком датасете (71%), на большом чуть-чуть улучшилась до 75% её почти догнал SMO (38% и 69% соотв.), random forest 43% -> 60%. Перефразируя известное выражение, - "использовать методы машинного обучения и готовить данные нужно не с помощью GUI, а головой" :(. 

# hw3
src: src/main/java/com/github/mikhailerofeev/nlp/hw3

test (один, тривиальный): src/test/java/com/github/mikhailerofeev/nlp/hw3/NaiveBayesClassifierUtilsTest.java


Аттрибуты брал из оставшегося с прошлого задания фильтра по Хи-квадрату

Результате на общем сете:

```
society	StatisticsResult{F1=0.25, precision=0.2, recall=0.333, accuracy=0.888, tp=1, tn=47, fp=4, fn=2}
politics	StatisticsResult{F1=0.285, precision=0.2, recall=0.5, accuracy=0.907, tp=1, tn=48, fp=4, fn=1}
auto	StatisticsResult{F1=0.5, precision=0.333, recall=1.0, accuracy=0.962, tp=1, tn=51, fp=2, fn=0}
accidents	StatisticsResult{F1=0.4, precision=0.5, recall=0.333, accuracy=0.833, tp=3, tn=42, fp=3, fn=6}
culture	StatisticsResult{F1=0.705, precision=1.0, recall=0.545, accuracy=0.907, tp=6, tn=43, fp=0, fn=5}
sport	StatisticsResult{F1=0.833, precision=0.833, recall=0.833, accuracy=0.962, tp=5, tn=47, fp=1, fn=1}
hi-tech	StatisticsResult{F1=0.307, precision=0.4, recall=0.25, accuracy=0.833, tp=2, tn=43, fp=3, fn=6}
science	StatisticsResult{F1=0.8, precision=0.666, recall=1.0, accuracy=0.962, tp=4, tn=48, fp=2, fn=0}
economics	StatisticsResult{F1=0.333, precision=0.2, recall=1.0, accuracy=0.925, tp=1, tn=49, fp=4, fn=0}
internet	StatisticsResult{F1=0.444, precision=0.4, recall=0.5, accuracy=0.907, tp=2, tn=47, fp=3, fn=2}

overall:	StatisticsResult{F1=0.514, precision=0.5, recall=0.53, accuracy=0.909, tp=26, tn=465, fp=26, fn=23}
```

Вывод: вау!

# hw4
src: /src/main/java/com/github/mikhailerofeev/nlp/hw4

tests (много кейсов, которые "берёт" программа): /src/test/java/com/github/mikhailerofeev/nlp/hw4

Качество на ```romip2005-facts``` всего-лишь ``` {F1=0.03, precision=0.15, recall=0.017, accuracy=0.015, tp=120, tn=0, fp=679, fn=6842} ```, на обработанных статьях accuracy 0.45

Очень много проблем из-за несогласованной работы ассессоров. Частично решена задача подмены на аббревиатуры на полное название, если оно уже встречалось. 

В процессе понял, что мой подход на регулярках либо тупик, либо архитектуру парсера нужно перерефакторить.

Не нашёл соотвествия между ```romip2005-facts``` и ```russian-ner```

# hw5


работа с тви и запуск классфикатора -- /src/main/java/com/github/mikhailerofeev/nlp/hw5

классификатор и Хи^2-тест -- /src/main/java/com/github/mikhailerofeev/nlp/hw3

Все слова:

- learn ```{F1=0.999, precision=1.0, recall=0.999, accuracy=0.999, tp=2009, tn=1836, fp=0, fn=2}``` -- тут скор очень большой наверняка из-за уникальных слов
- test ```{F1=0.656, precision=0.5, recall=0.957, accuracy=0.5, tp=90, tn=4, fp=90, fn=4}```

После фильтрации по p-value >= 0.7 (Хи^2), убирания редких (<4) слов и ручного фильтра:
- learn ```{F1=0.799, precision=0.682, recall=0.964, accuracy=0.747, tp=1939, tn=936, fp=900, fn=72}```
- test ```{F1=0.664, precision=0.5, recall=0.989, accuracy=0.5, tp=93, tn=1, fp=93, fn=1}```

learn получился хуже, тк классификатор жёг на уникальных словах. test немного улучшился, значит фильтрация помогла. Дальше нужно смотреть на данные, лучше нормализовать слова, строить биграммы и т.д.

более подробно реализованное задание, для другого курса: веб-сервис анализатор твитов на радость/злость/нейтральность/спам по произвольному запросу с возможностью разметки https://github.com/ktisha/python2012/tree/master/erofeev/TwitterAnalytic. Классификация типа наивного байеса.
