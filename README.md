# DAAR - Project 01: Clone de egrepDFA avec support partiel des ERE

### Compilation
1. create folder ```bin``` if not exists
2. run the command
    ```shell script
    make
    ```

### Execution

```shell script
java -jar egrepCLone.jar [option] [regular expression] [filename]
```

option
- **-n** : Precedes each line with its relative line number in the file.
- **-c** : Displays only a count of matching lines.
- **-d** : Force the use of the strategy with DFA only.

Exemple :
```shell script
java -jar egrepClone.jar -c "an.thing" "tests/A History of Babylon.txt"
```
