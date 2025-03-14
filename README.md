# parser_config

`parser_config` — это утилита, которая читает конфигурационные файлы, обрабатывает их в зависимости от указанных параметров и генерирует JSON-файл с результатами.

## Описание

1. На вход через командную строку должны подаваться два аргумента:
   - Путь к конфигурационному файлу.
   - Номер конфигурации, которую нужно обработать.

2. Решение должно прочитать конфигурационный файл, найти в нем конфигурацию по номеру и на ее основе продолжить обработку. В каждой конфигурации обязательными параметрами должны быть следующие:

   - `mode` со значением `dir` или `files`.
   - `path` или пути — путь к файлу или директории.

### Пример формата конфигурационного файла:

```txt
#id: номер конфигурации
#mode: dir или files
#path: путь или пути через запятую
#action: string
```

### Пример конфигурационного файла:

```txt
#id: 1
#mode: FILES
#path: C:\Users\samb2\IdeaProjects\parser_config\configTest\TextDocument(1).txt,C:\Users\samb2\IdeaProjects\parser_config\configTest\TextDocument(2).txt
#action: STRING

#id: 2
#mode: FILES
#path: C:\Users\samb2\IdeaProjects\parser_config\configTest\TextDocument(1).txt,C:\Users\samb2\IdeaProjects\parser_config\configTest\TextDocument(2).txt
#action: COUNT

#id: 3
#mode: FILES
#path: C:\Users\samb2\IdeaProjects\parser_config\configTest\TextDocument(3).txt,C:\Users\samb2\IdeaProjects\parser_config\configTest\TextDocument(2).txt
#action: REPLACE

#id: 4
#mode: DIRECTORIES
#path: C:\Users\samb2\IdeaProjects\parser_config\configTest
#action: STRING

#id: 4
#mode: DIRECTORIES
#path: C:\Users\samb2\IdeaProjects\parser_config\configTest
#action: COUNT

```

### Требования к реализации

1. **Чтение конфигурации**: Программа должна принимать два аргумента через командную строку:
    - Путь к конфигурационному файлу.
    - Номер конфигурации, которую нужно обработать.

2. **Обработка файлов**: В зависимости от конфигурации, программа должна правильно обрабатывать файлы:
    - Если режим `dir`, программа должна читать все файлы из директории.
    - Если режим `files`, программа должна читать указанные файлы.

3. **Действия**: В зависимости от выбранного действия (`string`, `count`, `replace`), программа должна формировать правильный результат для каждого файла:
    - **string**: Собирает строки из всех файлов поочередно.
    - **count**: Подсчитывает количество слов в каждой строке.
    - **replace**: Заменяет символы в строках с учетом номера файла.

4. **Вывод JSON**: После выполнения всех операций, программа должна сохранить результат в JSON-файл и вывести путь к нему.

### Пример выходного JSON-файла:

```json
{
  "configFile": "/home/user/myConfigs/config-5",
  "configurationID": "3",
  "configurationData": {
    "mode": "dir",
    "path": "/home/robot/filesToParse"
  },
  "out": {
    "1": {
      "1": "string1 taken from file1",
      "2": "string1 taken from file2",
      "3": "string1 taken from file3"
    },
    "2": {
      "1": "string2 taken from file1",
      "2": "string2 taken from file2",
      "3": "string2 taken from file3"
    },
    "3": {
      "1": "string3 taken from file1",
      "2": "",
      "3": "string3 taken from file3"
    }
  }
}
```

### Пояснения

1. Основные файлы:
   ```
   ..\parser_config\tester.txt - Это файл с тестовыми конфигурациями
   ..\parser_config\configTest - Это директория с текстом. (Если нужно запустить с консоли)
   ..\parser_config\result     - Это дериктория с json результатом.
   
   ```

2. Клонируйте репозиторий:
   ```bash
   git clone https://github.com/21092004Goda/parser_config.git
   
   ```

3. Запустите программу, передав аргументы:

   ```bash
   java -jar build/libs/parser_config-1.0-SNAPSHOT.jar ../path/parser_config/tester.txt 2

   ```
   Где:
    - `/path/to/config.txt` — путь к вашему конфигурационному файлу.
    - `1` — номер конфигурации, которую нужно обработать.


