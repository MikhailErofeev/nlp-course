package com.github.mikhailerofeev.nlp.hw1;

import com.google.common.base.Joiner;
import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;

import static junit.framework.Assert.assertEquals;

/**
 * @author Mikhail Erofeev https://github.com/MikhailErofeev
 * @since 16.03.14
 */
@RunWith(JUnit4.class)
public class SentenceParserTest {

    @Test
    public void testParseText1() throws Exception {
        File file = new File("src/test/resources/corpus/annot.opcorpora/id2-src.txt");
        File fileParsed = new File("src/test/resources/corpus/annot.opcorpora/id2.xml");
        final String srcString = FileUtils.readFileToString(file);
        final Text textReal = SentenceParser.parseText(srcString);
        final Text textExpected = StructuredTextParser.parseFile(fileParsed).get(0);
        assertEquals(Joiner.on('\n').join(textExpected.getSentences()), Joiner.on('\n').join(textReal.getSentences()));
    }

    @Test
    public void testParseInitials() throws Exception {
        final String srcString = "М.А. Ерофеев";
        final Text textReal = SentenceParser.parseText(srcString);
        final Text textExpected = new Text("М.А. Ерофеев");
        assertEquals(Joiner.on('\n').join(textExpected.getSentences()), Joiner.on('\n').join(textReal.getSentences()));
    }

    @Test
    public void testParseInitials2() throws Exception {
        final String srcString = "М. Ерофеев";
        final Text textReal = SentenceParser.parseText(srcString);
        final Text textExpected = new Text("М. Ерофеев");
        assertEquals(Joiner.on('\n').join(textExpected.getSentences()), Joiner.on('\n').join(textReal.getSentences()));
    }

    @Test
    public void testParseAddr() throws Exception {
        final String srcString = "по СПб и Лен. обл. в Гатч. р-не.";
        final Text textReal = SentenceParser.parseText(srcString);
        final Text textExpected = new Text("по СПб и Лен. обл. в Гатч. р-не.");
        assertEquals(Joiner.on('\n').join(textExpected.getSentences()), Joiner.on('\n').join(textReal.getSentences()));
    }

    @Test
    public void testManySymbols() throws Exception {
        final String srcString = "Почему??? Ну почему???!!";
        final Text textReal = SentenceParser.parseText(srcString);
        final Text textExpected = new Text("Почему???", "Ну почему???!!");
        assertEquals(Joiner.on('\n').join(textExpected.getSentences()), Joiner.on('\n').join(textReal.getSentences()));
    }

    @Test
    public void testParseAbbr() throws Exception {
        final String srcString = "стр.146 ст.2 3 ук рф";
        final Text textReal = SentenceParser.parseText(srcString);
        final Text textExpected = new Text("стр.146 ст.2 3 ук рф");
        assertEquals(Joiner.on('\n').join(textExpected.getSentences()), Joiner.on('\n').join(textReal.getSentences()));
    }

    @Test
    public void testParseBadAbbr() throws Exception {
        final String srcString = "Это стр. 146 ст.2 3 ук рф";
        final Text textReal = SentenceParser.parseText(srcString);
        final Text textExpected = new Text("Это стр. 146 ст.2 3 ук рф");
        assertEquals(Joiner.on('\n').join(textExpected.getSentences()), Joiner.on('\n').join(textReal.getSentences()));
    }

    @Test
    //uppercase after abbr. 1 sentences or 2?. Seems like first more likely 
    public void testParseAbbr2() throws Exception {
        final String srcString = "Ты, тов. Ерофеев так ничего и не понял.";
        final Text textReal = SentenceParser.parseText(srcString);
        final Text textExpected = new Text("Ты, тов. Ерофеев так ничего и не понял.");
        assertEquals(Joiner.on('\n').join(textExpected.getSentences()), Joiner.on('\n').join(textReal.getSentences()));
    }

    @Test
    public void testSingleWord() throws Exception {
        final String srcString = "Ночь. Улица. Фонарь. Удар кастета. Неясный крик и блики света";
        final Text textReal = SentenceParser.parseText(srcString);
        final Text textExpected = new Text("Ночь.", "Улица.", "Фонарь.", "Удар кастета.", "Неясный крик и блики света");
        assertEquals(Joiner.on('\n').join(textExpected.getSentences()), Joiner.on('\n').join(textReal.getSentences()));
    }

    @Test
    public void testQuotas() throws Exception {
        final String srcString = "В зал входит цитата. \"Вот и я!\"";
        final Text textReal = SentenceParser.parseText(srcString);
        final Text textExpected = new Text("В зал входит цитата.", "\"Вот и я!\"");
        assertEquals(Joiner.on('\n').join(textExpected.getSentences()), Joiner.on('\n').join(textReal.getSentences()));
    }

    @Test
    public void testBrackets() throws Exception {
        final String srcString = "В зал входият скоби (Привет! Вот и мы!).";
        final Text textReal = SentenceParser.parseText(srcString);
        final Text textExpected = new Text("В зал входият скоби (Привет! Вот и мы!).");
        assertEquals(Joiner.on('\n').join(textExpected.getSentences()), Joiner.on('\n').join(textReal.getSentences()));
    }

    @Test
    public void testBadTrim() throws Exception {
        final String srcString = "Первое . Второе.";
        final Text textReal = SentenceParser.parseText(srcString);
        final Text textExpected = new Text("Первое .", "Второе.");
        assertEquals(Joiner.on('\n').join(textExpected.getSentences()), Joiner.on('\n').join(textReal.getSentences()));
    }

    @Test
    public void testQuotasInside() throws Exception {
        final String srcString = "Текст «цитата внутри. содержит точки».";
        final Text textReal = SentenceParser.parseText(srcString);
        final Text textExpected = new Text("Текст «цитата внутри. содержит точки».");
        assertEquals(Joiner.on('\n').join(textExpected.getSentences()), Joiner.on('\n').join(textReal.getSentences()));
    }

    @Test
    public void testQuotaForManySentences() throws Exception {
        final String srcString = "«Это долгая цитата. Она затянется».";
        final Text textReal = SentenceParser.parseText(srcString);
        final Text textExpected = new Text("«Это долгая цитата.", "Она затянется».");
        assertEquals(Joiner.on('\n').join(textExpected.getSentences()), Joiner.on('\n').join(textReal.getSentences()));
    }

    @Test
    @Ignore
    public void testSentenceWithUpperCharInEnd() throws Exception {
        final String srcString = "Зачем-то закончил предложение с большой буквы Ы. И начал новое.";
        final Text textReal = SentenceParser.parseText(srcString);
        final Text textExpected = new Text("Зачем-то закончил предложение с большой буквы Ы.", "И начал новое.");
        assertEquals(Joiner.on('\n').join(textExpected.getSentences()), Joiner.on('\n').join(textReal.getSentences()));
    }

    @Test
    public void testIncorrectName() throws Exception {
        final String srcString = "Это М. Ерофеев.";
        final Text textReal = SentenceParser.parseText(srcString);
        final Text textExpected = new Text("Это М. Ерофеев.");
        assertEquals(Joiner.on('\n').join(textExpected.getSentences()), Joiner.on('\n').join(textReal.getSentences()));
    }

    @Test
    public void testInocrrenFIO() throws Exception {
        final String srcString = "Это М.А. Ерофеев.";
        final Text textReal = SentenceParser.parseText(srcString);
        final Text textExpected = new Text("Это М.А. Ерофеев.");
        assertEquals(Joiner.on('\n').join(textExpected.getSentences()), Joiner.on('\n').join(textReal.getSentences()));
    }

    @Test
    public void testInocrrenFIO2() throws Exception {
        final String srcString = "А Это Дж. Р. Р. Толкин.";
        final Text textReal = SentenceParser.parseText(srcString);
        final Text textExpected = new Text("А Это Дж. Р. Р. Толкин.");
        assertEquals(Joiner.on('\n').join(textExpected.getSentences()), Joiner.on('\n').join(textReal.getSentences()));
    }

    @Test
    public void testAbbrsInTheEnd() throws Exception {
        final String srcString = "текст, см. «Вконтакте» — «Одноклассники» — 1:0.";
        final Text textReal = SentenceParser.parseText(srcString);
        final Text textExpected = new Text("текст, см. «Вконтакте» — «Одноклассники» — 1:0.");
        assertEquals(Joiner.on('\n').join(textExpected.getSentences()), Joiner.on('\n').join(textReal.getSentences()));
    }

    @Test
    public void justDigest() throws Exception {
        final String srcString = "0.0. Забудь об интересах компании.";
        final Text textReal = SentenceParser.parseText(srcString);
        final Text textExpected = new Text("0.0. Забудь об интересах компании.");
        assertEquals(Joiner.on('\n').join(textExpected.getSentences()), Joiner.on('\n').join(textReal.getSentences()));
    }

    @Test
    @Ignore
    public void yearAbbr() throws Exception {
        final String srcString = "Последняя из них, династия Каджаров, была свергнута в 1925 г. Персидский язык";
        final Text textReal = SentenceParser.parseText(srcString);
        final Text textExpected = new Text("Последняя из них, династия Каджаров, была свергнута в 1925 г.", "Персидский язык");
        assertEquals(Joiner.on('\n').join(textExpected.getSentences()), Joiner.on('\n').join(textReal.getSentences()));
    }


    @Test
    @Ignore
    public void testList() throws Exception {
        final String srcString = "а) Где и как экранировать при выводе введенные пользователем поля. б) загрузка и вставка картинок";
        final Text textReal = SentenceParser.parseText(srcString);
        final Text textExpected = new Text("а) Где и как экранировать при выводе введенные пользователем поля.", "б) загрузка и вставка картинок");
        assertEquals(Joiner.on('\n').join(textExpected.getSentences()), Joiner.on('\n').join(textReal.getSentences()));
    }


}
