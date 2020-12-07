.PHONY: clean doc test

TARGET = bin
SOURCE = src
PACKAGE = daar
TEST = tests
DOC = doc
JAR = egrepClone


SOURCE_FILES = $(SOURCE)/daar/prj1/kmp/KMP.java \
    $(SOURCE)/daar/prj1/regex/Automaton.java \
    $(SOURCE)/daar/prj1/regex/RegEx.java \
    $(SOURCE)/daar/prj1/regex/RegExTree.java \
    $(SOURCE)/daar/prj1/Egrep.java \
    $(SOURCE)/daar/prj1/EgrepDFA.java \
    $(SOURCE)/daar/prj1/EgrepDFA.java \
    $(SOURCE)/daar/prj1/IRegexMatcher.java \
    $(SOURCE)/daar/prj1/Main.java


file: $(patsubst $(SOURCE)/%.java,$(TARGET)/%.class,$(SOURCE_FILES))

$(TARGET)/%.class: $(SOURCE)/%.java
	javac -sourcepath $(SOURCE) -cp $(TARGET) $^ -d $(TARGET)

run: file
	cd $(TARGET); java $(PACKAGE).prj1.Main "-n" "anything" "../tests/A History of Babylon.txt"

jar: manifest-ex $(JAR).jar

manifest-ex:
	@echo "Main-Class: daar.prj1.Main" >> manifest-ex

$(JAR).jar: file
	cd $(TARGET); jar cvfm ../$(JAR).jar ../manifest-ex $(PACKAGE); rm ../manifest-ex;


SOURCE_TEST = $(TEST)/daar/prj1/EgrepDFATest.java\
	$(TEST)/daar/prj1/EgrepTest.java \
	$(TEST)/daar/prj1/MainTest.java\
	$(TEST)/daar/prj1/kmp/KMPTest.java

test: $(patsubst $(TEST)/$(PACKAGE)/prj1/%.java,$(TEST)/%.class,$(SOURCE_TEST))

$(TEST)/%.class: $(TEST)/$(PACKAGE)/prj1/%.java
	javac -cp test-1.7.jar $(TARGET) $<
	java -jar test-1.7.jar $(subst $(TEST)/$(PACKAGE)/,$(PACKAGE)., $(subst .class,,$@)) &

$(TEST)/$(PACKAGE)/prj1/%.class: $(TEST)/$(PACKAGE)/prj1/%.java
	javac -cp test-1.7.jar $<
	java -jar test-1.7.jar $(subst $(TEST)/$(PACKAGE)/prj1/,$(PACKAGE)., $(subst .class,,$@)) &

$(TEST)/$(TARGET):
	mkdir -p $@



clean:
	rm -frv $(TARGET)/*
