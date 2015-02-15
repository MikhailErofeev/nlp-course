package com.github.mikhailerofeev.nlp.hw4;

import com.github.mikhailerofeev.nlp.hw4.v1.Fact;
import com.github.mikhailerofeev.nlp.hw4.v1.FactsRetriever;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(JUnit4.class)
public class FactsRetrieverTest {


    @Test
    public void testRetrieve1() {
        String text = "Представитель \"Равноправия\" Мирослав Митрофанов сообщил агентству LETA, что сейчас юристы оценивают, имеет ли смысл после создания ПЧЕЛ продолжать дальнейшую деятельность составляющих его партий";
        Fact expected = new Fact("РАВНОПРАВИЕ", "Митрофанов Мирослав", -1, -1, -1, "", -1);
        final List<Fact> retrieve = FactsRetriever.retrieveStatic(text);
        assertEquals(retrieve.toString(), 1, retrieve.size());
        assertEqualsFacts(expected, retrieve.get(0));
    }

    @Test
    public void testRetrieve2() {
        String text = "Лидеру социалистов Альфреду Рубиксу путь туда закрыт из-за судимости, поэтому наиболее острая конкуренция предполагается между лидером \"Равноправия\" Татьяной Жданок и кандидатом от ПНС Борисом Цилевичем\n";
        Fact expected1 = new Fact("РАВНОПРАВИЕ", "Татьяна Жданок", -1, -1, -1, "", -1);
        Fact expected2 = new Fact("ПНС", "Борис Цилевич", -1, -1, -1, "", -1);
        final List<Fact> retrieve = FactsRetriever.retrieveStatic(text);
        assertEquals(retrieve.toString(), 2, retrieve.size());
        assertEqualsFacts(expected1, retrieve.get(0));
        assertEqualsFacts(expected2, retrieve.get(1));
    }

    @Test
    public void testRetrieve3() {
        String text = "Заместитель начальника Бюро по предотвращению и борьбе с коррупцией (БПБК) Валдис Пумпурс лишен доступа к информации, содержащей государственную тайну, и в связи с этим отстранен от должности\n";
        Fact expected = new Fact("Бюро по предотвращению и борьбе с коррупцией", "Валдис Пумпурс", -1, -1, -1, "", -1);
        final List<Fact> retrieve = FactsRetriever.retrieveStatic(text);
        assertEquals(1, retrieve.size());
        assertEqualsFacts(expected, retrieve.get(0));
    }


    @Test
    public void testRetrieve4() {
        String text = "\" - сказал председатель СГН Владимир Соколов газете \"Вести сегодня\"";
        Fact expected = new Fact("СГН", "Владимир Соколов", -1, -1, -1, "", -1);
        final List<Fact> retrieve = FactsRetriever.retrieveStatic(text);
        assertEquals(1, retrieve.size());
        assertEqualsFacts(expected, retrieve.get(0));
    }

    @Test
    public void testRetrieve5() {
        String text = "президенту Латвии Вайре Вике-Фрейберге в надежде";
        Fact expected = new Fact("Латвия", "Вайре Вике-Фрейберге ", -1, -1, -1, "", -1);
        final List<Fact> retrieve = FactsRetriever.retrieveStatic(text);
        assertEquals(1, retrieve.size());
        assertEqualsFacts(expected, retrieve.get(0));
    }

    @Test
    public void testRetrieve6() {
        String text1 = "Охрана государственной тайны и первые отставки Заместитель начальника Бюро по предотвращению и борьбе с коррупцией (БПБК) Валдис Пумпурс лишен доступа к информации, содержащей государственную тайну, и в связи с этим отстранен от должности\n";
        String realFullName = "Бюро по предотвращению и борьбе с коррупцией";
        Fact expected = new Fact(realFullName, "Валдис Пумпурс", -1, -1, -1, "", -1);
        FactsRetriever factsRetriever = new FactsRetriever();
        final List<Fact> retrieve = factsRetriever.retrieve(text1);
        assertEquals(1, retrieve.size());
        assertEqualsFacts(expected, retrieve.get(0));

        String fullName = factsRetriever.getAbbr2FullName().get("БПБК");
        assertEquals(realFullName, fullName);

        String text2 = "директора БПБК Ютой Стрике";
        Fact expected2 = new Fact(realFullName, "Ютой Стрике", -1, -1, -1, "", -1);
        final List<Fact> retrieve2 = factsRetriever.retrieve(text2);
        assertEquals(1, retrieve2.size());
        assertEqualsFacts(expected2, retrieve2.get(0));

    }

    @Test
    public void testRetrieve7() {
        String text = "Председателем правления BITE стал депутат Яков Плинер, который";
        Fact expected = new Fact("BITE", "Яков Плинер", -1, -1, -1, "", -1);
        final List<Fact> retrieve = FactsRetriever.retrieveStatic(text);
        assertEquals(1, retrieve.size());
        assertEqualsFacts(expected, retrieve.get(0));
    }

    @Test
    public void testRetrieve8() {
        String text = "У президента LMT Юриса Бинде свой взгляд на эти проблемы.";
        Fact expected = new Fact("LMT", "Юрису Бинде", -1, -1, -1, "", -1);
        final List<Fact> retrieve = FactsRetriever.retrieveStatic(text);
        assertEquals(1, retrieve.size());
        assertEqualsFacts(expected, retrieve.get(0));
    }

    @Test
    public void testRetrieve9() {
        String text = "После саммита Россия-ЕС в Риме министр иностранных дел Латвии Сандра Калниете прокомментировала его результаты газете \"Телеграф\".";
        Fact expected = new Fact("Латвия", "Сандра Калниете", -1, -1, -1, "", -1);
        final List<Fact> retrieve = FactsRetriever.retrieveStatic(text);
        assertEquals(1, retrieve.size());
        assertEqualsFacts(expected, retrieve.get(0));
    }

    @Test
    public void testRetrieve10() {
        String text = "там были Миша Ерофеев и Имя Фамилия.";
        final List<Fact> retrieve = FactsRetriever.retrieveStatic(text);
        assertEquals(0, retrieve.size());
    }

    @Test
    public void testRetrieve11() {
        String text = "Новый состав совета: сопредседатели - Татьяна Жданок и Яков Плинер, парламентарий Владимир Бузаев";
        final List<Fact> retrieve = FactsRetriever.retrieveStatic(text);
        assertEquals(0, retrieve.size());
    }

    @Test
    public void testRetrieve12() {
        String text = "Пресс-секретарь Бюро по защите Сатверсме (БЗС) Дайнис Микелсонс подтвердил агентству LETA";
        Fact expected = new Fact("Бюро по защите Сатверсме", "Дайнис Микелсонс", -1, -1, -1, "", -1);
        final List<Fact> retrieve = FactsRetriever.retrieveStatic(text);
        assertEquals(1, retrieve.size());
        assertEqualsFacts(expected, retrieve.get(0));
    }

    @Test
    public void testRetrieve13() {
        String text = "названные в интервью Kb Кокоревичем (президент Rigas Fondu birza Гунтар Кокоревич - прим. ИА REGNUM ) ";
        Fact expected = new Fact("Rigas Fondu birza", "Гунтар Кокоревич", -1, -1, -1, "", -1);
        final List<Fact> retrieve = FactsRetriever.retrieveStatic(text);
        assertEquals(1, retrieve.size());
        assertEqualsFacts(expected, retrieve.get(0));
    }


    @Test
    @Ignore
    public void testRetrieve14() {
        String text = "Перед депутатами выступил председатель региональной энергетической комиссии - генеральный директор департамента цен и тарифов С.Милованов, который представил проект предельных тарифов на 2004 год.";
        Fact expected = new Fact("региональная энергетическая коммисия", "Милован С.", -1, -1, -1, "", -1);
        final List<Fact> retrieve = FactsRetriever.retrieveStatic(text);
        assertEquals(1, retrieve.size());
        assertEqualsFacts(expected, retrieve.get(0));
    }

    @Test
    public void testRetrieve15() {
        String text = "Кроме того, для участников конкурса организованы семинарские занятия, где формы и методы защиты общества от наркомании, роль телевидения в формировании здорового образа жизни, другие вопросы обсуждались при участии ведущих - советника замминистра РФ по делам печати, телерадиовещания и средств массовых коммуникаций Бориса Брацыло и заведующего кафедрой телевидения и радио факультета журналистики МГУ Георгия Кузнецова.";
        Fact expected = new Fact("МГУ", "Георгия Кузнец", -1, -1, -1, "", -1);
        final List<Fact> retrieve = FactsRetriever.retrieveStatic(text);
        assertEquals(1, retrieve.size());
        assertEqualsFacts(expected, retrieve.get(0));
    }
    
    @Test
    public void testRetrieve16() {
        String text = "Как сообщает Новое телевидение Кубани, об этом заявил глава Госстроя РФ Николай Кошман.";
        Fact expected = new Fact("Госстр", "Николай Кошман", -1, -1, -1, "", -1);
        final List<Fact> retrieve = FactsRetriever.retrieveStatic(text);
        assertEquals(1, retrieve.size());
        assertEqualsFacts(expected, retrieve.get(0));
    }
    
    @Test
    public void testRetrieve17() {
        String text = "Председатель Совета Федерации согласился ответить на вопросы главного редактора газеты \"Кубанские новости\" Евгения ФИЛИМОНОВА. -";
        Fact expected = new Fact("Кубанские новости", "Евгения ФИЛИМОНОВА", -1, -1, -1, "", -1);
        final List<Fact> retrieve = FactsRetriever.retrieveStatic(text);
        assertEquals(1, retrieve.size());
        assertEqualsFacts(expected, retrieve.get(0));
    }
        
    @Test
    public void testRetrieve18() {
        String text = "Главе администрации Геленджика С.Озерову пришло письмо с просьбой предоставить земельный участок площадью 3-4 гектара в районе лесного массива Голубая даль в селе Дивноморское под строительство резиденции патриарха Московского и Всея Руси Алексия.";
        Fact expected = new Fact("администрация Геленджика", "Озеров С.", -1, -1, -1, "", -1);
        final List<Fact> retrieve = FactsRetriever.retrieveStatic(text);
        assertEquals(1, retrieve.size());
        assertEqualsFacts(expected, retrieve.get(0));
    }

    private static void assertEqualsFacts(Fact expected, Fact actual) {
        if (!expected.weakEquals(actual)) {
            throw new IllegalStateException(expected + "\t" + actual);
        }
    }

}