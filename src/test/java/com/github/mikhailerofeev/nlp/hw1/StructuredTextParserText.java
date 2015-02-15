package com.github.mikhailerofeev.nlp.hw1;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Mikhail Erofeev https://github.com/MikhailErofeev
 * @since 16.03.14
 */

@RunWith(JUnit4.class)
public class StructuredTextParserText {

  @Test
  public void testCreateTextObject() throws FileNotFoundException, XMLStreamException {
    File file = new File("src/test/resources/corpus/annot.opcorpora/id2.xml");
    assertTrue(file.exists());
    final List<Text> texts = StructuredTextParser.parseFile(file);
    assertEquals(1, texts.size());
    assertEquals(44, texts.get(0).getSentences().size());
  }

  @Test
  public void testCreateManyTexts() throws FileNotFoundException, XMLStreamException {
    File file = new File("src/test/resources/corpus/annot.opcorpora/ids2-4.xml");
    assertTrue(file.exists());
    final List<Text> texts = StructuredTextParser.parseFile(file);
    assertEquals(3, texts.size());
    int size = 0;
    for (Text t : texts) {
      size += t.getSentences().size();
    }
    assertEquals(116, size);
  }
}
