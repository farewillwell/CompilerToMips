package front_end;

import java.util.HashMap;

public class ErrorCollector {
    private final HashMap<Integer, String> lineError;

    public static ErrorCollector EC = new ErrorCollector();

    private int allLine;

    public ErrorCollector() {
        lineError = new HashMap<>();
        this.allLine = 0;
    }

    public void setAllLine(int line) {
        this.allLine = line;
    }

    public void addError(int line, String errorType) {
        if (lineError.containsKey(line)) {
            throw new RuntimeException("line error dub! at " + line + " the old is " + lineError.get(line) + " new error is " + errorType);
        }
        lineError.put(line, errorType);
    }

    public void printErrors() {
        for (int i = 1; i <= allLine; i++) {
            if (lineError.containsKey(i)) {
                System.out.println(i + " " + lineError.get(i));
            }
        }
    }

    public boolean noError() {
        return lineError.size() == 0;
    }
}
