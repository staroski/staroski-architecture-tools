package br.com.staroski.io;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;

/**
 * Utility class that allows chaining of {@link PrintStream}s so they can be used as a single object.<br>
 * It is an implementation of the <I>Composite</I> design pattern.
 *
 * @author Staroski, Ricardo Artur
 */
public final class PrintStreams extends PrintStream {

    // The implementation of this class is based on AWTEventMulticaster
    // Allowing method calls to be propagated to multiple objects as if they were a
    // single one
    // This eliminates the need to write loops to iterate over the objects

    // An OutputStream that does nothing used as parameter to the superclass
    private static final OutputStream EMPTY_OUTPUT = new OutputStream() {

        @Override
        public void write(int b) throws IOException {}
    };

    public static PrintStream attach(PrintStream printStreamExistente, PrintStream printStreamParaAdicionar) {
        return internalAttach(printStreamExistente, printStreamParaAdicionar);
    }

    public static PrintStream detach(PrintStream printStreamExistente, PrintStream printStreamParaRemover) {
        return internalDetach(printStreamExistente, printStreamParaRemover);
    }

    private static PrintStream internalAttach(PrintStream printStreamExistente, PrintStream printStreamParaAdicionar) {
        if (printStreamExistente == null) {
            return printStreamParaAdicionar;
        }
        if (printStreamParaAdicionar == null) {
            return printStreamExistente;
        }
        return new PrintStreams(printStreamExistente, printStreamParaAdicionar);
    }

    private static PrintStream internalDetach(PrintStream printStreamExistente, PrintStream printStreamParaRemover) {
        if (printStreamExistente == printStreamParaRemover || printStreamExistente == null) {
            return null;
        }
        if (printStreamExistente instanceof PrintStreams) {
            PrintStreams tupla = (PrintStreams) printStreamExistente;
            if (printStreamParaRemover == tupla.a) {
                return tupla.b;
            }
            if (printStreamParaRemover == tupla.b) {
                return tupla.a;
            }
            PrintStream a = internalDetach(tupla.a, printStreamParaRemover);
            PrintStream b = internalDetach(tupla.b, printStreamParaRemover);
            if (a == tupla.a && b == tupla.b) {
                return tupla;
            }
            return internalAttach(a, b);
        }
        return printStreamExistente;
    }

    private final PrintStream a;
    private final PrintStream b;

    private PrintStreams(PrintStream a, PrintStream b) {
        super(EMPTY_OUTPUT);
        this.a = a;
        this.b = b;
    }

    @Override
    public PrintStream append(char value) {
        a.append(value);
        b.append(value);
        return this;
    }

    @Override
    public PrintStream append(CharSequence text) {
        a.append(text);
        b.append(text);
        return this;
    }

    @Override
    public PrintStream append(CharSequence text, int start, int end) {
        a.append(text, start, end);
        b.append(text, start, end);
        return this;
    }

    @Override
    public boolean checkError() {
        boolean error = false;
        error |= a.checkError();
        error |= b.checkError();
        return error;
    }

    @Override
    public void close() {
        a.close();
        b.close();
    }

    @Override
    public void flush() {
        a.flush();
        b.flush();
    }

    @Override
    public PrintStream format(Locale l, String format, Object... args) {
        a.format(l, format, args);
        b.format(l, format, args);
        return this;
    }

    @Override
    public PrintStream format(String format, Object... args) {
        a.format(format, args);
        b.format(format, args);
        return this;
    }

    @Override
    public void print(boolean value) {
        a.print(value);
        b.print(value);
    }

    @Override
    public void print(char value) {
        a.print(value);
        b.print(value);
    }

    @Override
    public void print(char[] value) {
        a.print(value);
        b.print(value);
    }

    @Override
    public void print(double value) {
        a.print(value);
        b.print(value);
    }

    @Override
    public void print(float value) {
        a.print(value);
        b.print(value);
    }

    @Override
    public void print(int value) {
        a.print(value);
        b.print(value);
    }

    @Override
    public void print(long value) {
        a.print(value);
        b.print(value);
    }

    @Override
    public void print(Object value) {
        a.print(value);
        b.print(value);
    }

    @Override
    public void print(String value) {
        a.print(value);
        b.print(value);
    }

    @Override
    public PrintStream printf(Locale l, String format, Object... args) {
        a.printf(l, format, args);
        b.printf(l, format, args);
        return this;
    }

    @Override
    public PrintStream printf(String format, Object... args) {
        a.printf(format, args);
        b.printf(format, args);
        return this;
    }

    @Override
    public void println() {
        a.println();
        b.println();
    }

    @Override
    public void println(boolean value) {
        a.println(value);
        b.println(value);
    }

    @Override
    public void println(char value) {
        a.println(value);
        b.println(value);
    }

    @Override
    public void println(char[] value) {
        a.println(value);
        b.println(value);
    }

    @Override
    public void println(double value) {
        a.println(value);
        b.println(value);
    }

    @Override
    public void println(float value) {
        a.println(value);
        b.println(value);
    }

    @Override
    public void println(int value) {
        a.println(value);
        b.println(value);
    }

    @Override
    public void println(long value) {
        a.println(value);
        b.println(value);
    }

    @Override
    public void println(Object value) {
        a.println(value);
        b.println(value);
    }

    @Override
    public void println(String value) {
        a.println(value);
        b.println(value);
    }

    @Override
    public void write(byte[] bytes) throws IOException {
        a.write(bytes);
        b.write(bytes);
    }

    @Override
    public void write(byte[] bytes, int offset, int length) {
        a.write(bytes, offset, length);
        b.write(bytes, offset, length);
    }

    @Override
    public void write(int value) {
        a.write(value);
        b.write(value);
    }
}