package com.github.mikhailerofeev.nlp.hw1;

import com.google.common.collect.Lists;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.*;
import java.util.Iterator;
import java.util.List;


/**
 * @author Mikhail Erofeev https://github.com/MikhailErofeev
 * @since 16.03.14
 */

public class StructuredTextParser {
  public static Iterator<Text> parseFileIterable(File file) throws FileNotFoundException, XMLStreamException {
    InputStream is = new BufferedInputStream(new FileInputStream(file));
    XMLInputFactory factory = XMLInputFactory.newInstance();
    XMLStreamReader reader = factory.createXMLStreamReader(is);
    return new TextIterator(reader);
  }

  public static List<Text> parseFile(File file) throws FileNotFoundException, XMLStreamException {
    return Lists.newArrayList(parseFileIterable(file));
  }

  static class TextIterator implements Iterator<Text> {
    private final XMLStreamReader reader;

    private Text next = null;

    public TextIterator(XMLStreamReader reader) {
      this.reader = reader;
    }

    @Override
    public boolean hasNext() {
      //holy shit
      if (next == null) {
        next = next();
      }
      return next != null;
    }

    @Override
    public Text next() {
      try {
        if (next == null) {
          return nextUnsafe();
        } else {
          final Text nextTmp = next;
          next = null;
          return nextTmp;
        }
      } catch (XMLStreamException e) {
        throw new RuntimeException(e);
      }
    }

    private Text nextUnsafe() throws XMLStreamException {
      Text text = null;
      while (reader.hasNext()) {
        int event = reader.next();
        switch (event) {
          case XMLStreamConstants.START_ELEMENT: {
            if ("text".equals(reader.getLocalName())) {
              if (text != null) {
                throw new IllegalStateException("text already inited");
              }
              text = new Text();
            } else if ("source".equals(reader.getLocalName())) {
              reader.next();
              //noinspection ConstantConditions
              text.addSentence(reader.getText().trim());
            }
            break;
          }
          case XMLStreamConstants.END_ELEMENT: {
            if ("text".equals(reader.getLocalName())) {
              if (text == null) {
                throw new IllegalStateException("miss text start");
              }
              return text;
            }
          }
          break;
        }
      }
      return null;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }
}
