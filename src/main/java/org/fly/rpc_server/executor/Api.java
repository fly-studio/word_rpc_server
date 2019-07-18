package org.fly.rpc_server.executor;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.hankcs.hanlp.corpus.document.sentence.Sentence;
import com.hankcs.hanlp.dictionary.py.Pinyin;
import com.hankcs.hanlp.seg.common.Term;

import java.io.IOException;
import java.util.List;

public class Api {

    public static ObjectMapper objectMapper = new ObjectMapper();
    static {
        SimpleModule module = new SimpleModule();
        module.addSerializer(new TermSerializer(Term.class));
        module.addSerializer(new PinyinSerializer(Pinyin.class));
        module.addSerializer(new SentenceSerializer(Sentence.class));
        objectMapper.registerModule(module);
    }

    private static class TermSerializer extends StdSerializer<Term> {

        TermSerializer(Class<Term> t) {
            super(t);
        }

        @Override
        public void serialize(Term term, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("word", term.word);
            jsonGenerator.writeNumberField("offset", term.offset);
            jsonGenerator.writeStringField("nature", term.nature.toString());
            jsonGenerator.writeEndObject();
        }
    }
    
    private static class PinyinSerializer extends StdSerializer<Pinyin> {
        PinyinSerializer(Class<Pinyin> t) {
            super(t);
        }

        @Override
        public void serialize(Pinyin pinyin, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();
            jsonGenerator.writeStringField("name", pinyin.toString());
            jsonGenerator.writeStringField("firstChar", String.valueOf(pinyin.getFirstChar()));
            jsonGenerator.writeStringField("shengmu", pinyin.getShengmu().toString());
            jsonGenerator.writeStringField("yunmu", pinyin.getYunmu().toString());
            jsonGenerator.writeNumberField("tone", pinyin.getTone());
            jsonGenerator.writeEndObject();
        }
    }

    private static class SentenceSerializer extends StdSerializer<Sentence> {
        SentenceSerializer(Class<Sentence> t) {
            super(t);
        }

        @Override
        public void serialize(Sentence sentence, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString(sentence.toString());
        }
    }
    
    public static class HanLP {
        public static List<Term> segment(String content)
        {
            return com.hankcs.hanlp.HanLP.segment(content);
        }

        public static String convertToSimplifiedChinese(String traditionalChineseString)
        {
            return com.hankcs.hanlp.HanLP.convertToSimplifiedChinese(traditionalChineseString);
        }

        public static String convertToTraditionalChinese(String simplifiedChineseString) 
        {
            return com.hankcs.hanlp.HanLP.convertToTraditionalChinese(simplifiedChineseString);
        }

        public static String s2t(String s)
        {
            return com.hankcs.hanlp.HanLP.s2t(s);
        }
        
        public static String t2s(String s)
        {
            return com.hankcs.hanlp.HanLP.t2s(s);
        }

        public static String s2tw(String s) 
        {
            return com.hankcs.hanlp.HanLP.s2tw(s);
        }

        public static String tw2s(String tw)
        {
            return com.hankcs.hanlp.HanLP.tw2s(tw);
        }

        public static String s2hk(String s)
        {
            return com.hankcs.hanlp.HanLP.s2hk(s);
        }

        public static String hk2s(String hk)
        {
            return com.hankcs.hanlp.HanLP.hk2s(hk);
        }

        public static String t2tw(String t)
        {
            return com.hankcs.hanlp.HanLP.t2tw(t);
        }

        public static String tw2t(String tw)
        {
            return com.hankcs.hanlp.HanLP.tw2t(tw);
        }

        public static String t2hk(String t)
        {
            return com.hankcs.hanlp.HanLP.t2hk(t);
        }

        public static String hk2t(String hk)
        {
            return com.hankcs.hanlp.HanLP.hk2t(hk);
        }

        public static String hk2tw(String hk)
        {
            return com.hankcs.hanlp.HanLP.hk2tw(hk);
        }

        public static String tw2hk(String tw)
        {
            return com.hankcs.hanlp.HanLP.tw2hk(tw);
        }

        public static String convertToPinyinString(String text, String separator, String remainNoneBoolean)
        {
            return com.hankcs.hanlp.HanLP.convertToPinyinString(text, separator, Boolean.valueOf(remainNoneBoolean));
        }

        public static List<Pinyin> convertToPinyinList(String text)
        {
            return com.hankcs.hanlp.HanLP.convertToPinyinList(text);
        }

        public static String convertToPinyinFirstCharString(String text, String separator, String remainNoneBoolean)
        {
            return com.hankcs.hanlp.HanLP.convertToPinyinFirstCharString(text, separator, Boolean.valueOf(remainNoneBoolean));
        }

        public static List<String> extractPhrase(String text, String sizeInt)
        {
            return com.hankcs.hanlp.HanLP.extractPhrase(text, Integer.valueOf(sizeInt));
        }

        public static List<String> extractKeyword(String document, String sizeInt)
        {
            return com.hankcs.hanlp.HanLP.extractKeyword(document, Integer.valueOf(sizeInt));
        }

        public static List<String> extractSummary(String document, String sizeInt)
        {
            return com.hankcs.hanlp.HanLP.extractSummary(document, Integer.valueOf(sizeInt));
        }

        public static List<String> extractSummary(String document, String sizeInt, String sentence_separator)
        {
            return com.hankcs.hanlp.HanLP.extractSummary(document, Integer.valueOf(sizeInt), sentence_separator);
        }

        public static String getSummary(String document, String max_lengthInt)
        {
            return com.hankcs.hanlp.HanLP.getSummary(document, Integer.valueOf(max_lengthInt));
        }

        public static String getSummary(String document, String max_lengthInt, String sentence_separator)
        {
            return com.hankcs.hanlp.HanLP.getSummary(document, Integer.valueOf(max_lengthInt), sentence_separator);
        }
        
    } 

    public static class StandardTokenizer {
        public static List<Term> segment(String content)
        {
            return  com.hankcs.hanlp.tokenizer.StandardTokenizer.segment(content);
        }

        public static List<List<Term>> seg2sentence(String content)
        {
            return  com.hankcs.hanlp.tokenizer.StandardTokenizer.seg2sentence(content);
        }

        public static List<List<Term>> seg2sentence(String content, String shortestBoolean)
        {
            return  com.hankcs.hanlp.tokenizer.StandardTokenizer.seg2sentence(content, Boolean.valueOf(shortestBoolean));
        }
    }

    public static class NLPTokenizer {
        public static List<Term> segment(String content)
        {
            return  com.hankcs.hanlp.tokenizer.NLPTokenizer.segment(content);
        }

        public static List<List<Term>> seg2sentence(String content)
        {
            return com.hankcs.hanlp.tokenizer.NLPTokenizer.seg2sentence(content);
        }

        public static List<List<Term>> seg2sentence(String content, String shortestBoolean)
        {
            return  com.hankcs.hanlp.tokenizer.NLPTokenizer.seg2sentence(content, Boolean.valueOf(shortestBoolean));
        }

        public static Sentence analyze(String content)
        {
            return com.hankcs.hanlp.tokenizer.NLPTokenizer.analyze(content);
        }

        public static Sentence analyze(String content, String translateLabelsBoolean)
        {
            return Boolean.valueOf(translateLabelsBoolean) ? com.hankcs.hanlp.tokenizer.NLPTokenizer.analyze(content).translateLabels() : analyze(content);
        }
    }

    public static class IndexTokenizer {
        public static List<Term> segment(String content)
        {
            return  com.hankcs.hanlp.tokenizer.IndexTokenizer.segment(content);
        }

        public static List<List<Term>> seg2sentence(String content)
        {
            return  com.hankcs.hanlp.tokenizer.IndexTokenizer.seg2sentence(content);
        }

        public static List<List<Term>> seg2sentence(String content, String shortestBoolean)
        {
            return  com.hankcs.hanlp.tokenizer.IndexTokenizer.seg2sentence(content, Boolean.valueOf(shortestBoolean));
        }
    }

    public static class NShortSegment {
        public static List<Term> seg(String content)
        {
            return new com.hankcs.hanlp.seg.NShort.NShortSegment().enableCustomDictionary(true).enablePlaceRecognize(true).enableOrganizationRecognize(true).seg(content);
        }

        public static List<List<Term>> seg2sentence(String content)
        {
            return new com.hankcs.hanlp.seg.NShort.NShortSegment().enableCustomDictionary(true).enablePlaceRecognize(true).enableOrganizationRecognize(true).seg2sentence(content);
        }

        public static List<List<Term>> seg2sentence(String content, String shortestBoolean)
        {
            return new com.hankcs.hanlp.seg.NShort.NShortSegment().enableCustomDictionary(true).enablePlaceRecognize(true).enableOrganizationRecognize(true).seg2sentence(content, Boolean.valueOf(shortestBoolean));
        }
    }

    public static class CRFLexicalAnalyzer {
        public static String analyze(String content) throws IOException
        {
            return new com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer().analyze(content).toString();
        }

        public static List<String> segment(String sentence) throws IOException
        {
            return  new com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer().segment(sentence);
        }

        public static List<String> segment(String sentence, String normalized) throws IOException
        {
            return  new com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer().segment(sentence, normalized);
        }
    }

    public static class SpeedTokenizer {
        public static String segment(String content)
        {
            return SpeedTokenizer.segment(content);
        }
    }

    public static class CustomDictionary {
        public static void add(String word)
        {
            com.hankcs.hanlp.dictionary.CustomDictionary.add(word);
        }

        public static void add(String word, String natureWithFrequency)
        {
            com.hankcs.hanlp.dictionary.CustomDictionary.add(word, natureWithFrequency);
        }

        public static boolean insert(String word, String natureWithFrequency)
        {
            return com.hankcs.hanlp.dictionary.CustomDictionary.insert(word, natureWithFrequency);
        }

        public static boolean insert(String word)
        {
            return com.hankcs.hanlp.dictionary.CustomDictionary.insert(word);
        }

        public static void remove(String key)
        {
            com.hankcs.hanlp.dictionary.CustomDictionary.remove(key);
        }


    }
}
