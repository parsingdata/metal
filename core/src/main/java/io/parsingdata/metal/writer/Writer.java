package io.parsingdata.metal.writer;

import java.io.OutputStream;
import java.lang.reflect.Field;

import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Def;
import io.parsingdata.metal.token.Seq;
import io.parsingdata.metal.token.Token;

public class Writer {

    public static void write(final Token token, final Object obj, final OutputStream out, final Encoding encoding) throws Exception {
        if (token instanceof Seq) {
            final Seq seq = (Seq) token;
            for (final Token child : seq.tokens()) {
                write(child, obj, out, child.encoding != null ? child.encoding : encoding);
            }
        }
        else if (token instanceof Def) {
            final Def def = (Def) token;
            write(obj, def.name, out, def.encoding != null ? def.encoding : encoding);
        }
    }

    public static void write(final Object obj, final String name, final OutputStream out, final Encoding encoding) throws Exception {
        // TODO argnotnull
        for (final Field field : obj.getClass().getFields()) {
            final Name annotation = field.getAnnotation(Name.class);
            if (annotation != null && name.equals(annotation.value())) {
                write(obj, field, out, encoding);
            }
        }
    }

    private static void write(final Object obj, final Field field, final OutputStream out, final Encoding encoding) throws Exception {
        if (field.getType() == String.class) {
            final String value = (String) field.get(obj);
            if (value != null) {
                out.write(value.getBytes(encoding != null && encoding.charset != null ? encoding.charset : Encoding.DEFAULT_CHARSET));
            }
        }
        else if (field.getType().getTypeName().equals("byte")) {
            out.write((byte) field.get(obj));
        }
    }
}
