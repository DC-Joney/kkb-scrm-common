/*
 * Copyright
 */
package com.kkb.common.tools.text;

import java.util.HashMap;
import java.util.Map;

/**
 * 文本格式化 - (大括号 {} 值映射)
 *
 * @author ztkool
 * @version v
 */
final class TextFormatter {

    private TextFormatter() {
    }

    public static TextMeta format(String pattern, Object... args) {
        TextMeta meta = new TextMeta(pattern, args);
        if (null == pattern || null == args) {
            return meta.message(pattern);
        } else {
            int index = 0;
            int capacity = pattern.length() + 50;
            StringBuilder builder = new StringBuilder(capacity);
            for (int argIndex = 0; argIndex < args.length; argIndex++) {
                Object arg = args[argIndex];
                int braceIndex = pattern.indexOf("{}", index);
                if (braceIndex == -1) {
                    if (index == 0) {
                        return meta.message(pattern);
                    }
                    builder.append(pattern, index, pattern.length());
                    return meta.message(builder.toString());
                }
                if (isEscapedBrace(pattern, braceIndex)) {
                    if (!isDoubleEscaped(pattern, braceIndex)) {
                        builder.append(pattern, index, braceIndex - 1);
                        builder.append("{");
                        argIndex--;
                        index = braceIndex + 1;
                    } else {
                        builder.append(pattern, index, braceIndex - 1);
                        deeplyAppendParameter(builder, arg, new HashMap<>(8));
                        index = braceIndex + 2;
                    }
                } else {
                    builder.append(pattern, index, braceIndex);
                    deeplyAppendParameter(builder, arg, new HashMap<>(8));
                    index = braceIndex + 2;
                }
            }
            builder.append(pattern, index, pattern.length());
            return meta.message(builder.toString());
        }
    }

    static final boolean isEscapedBrace(String pattern, int braceStartIndex) {
        if (braceStartIndex == 0) {
            return false;
        } else {
            char potentialEscape = pattern.charAt(braceStartIndex - 1);
            return potentialEscape == '\\';
        }
    }

    static final boolean isDoubleEscaped(String pattern, int braceStartIndex) {
        return braceStartIndex >= 2
                && pattern.charAt(braceStartIndex - 2) == '\\';
    }

    private static void deeplyAppendParameter(StringBuilder builder, Object o, Map<Object[], Object> seenMap) {
        if (o == null) {
            builder.append("null");
        } else {
            if (!o.getClass().isArray()) {
                safeObjectAppend(builder, o);
            } else if (o instanceof boolean[]) {
                booleanArrayAppend(builder, ((boolean[]) o));
            } else if (o instanceof byte[]) {
                byteArrayAppend(builder, ((byte[]) o));
            } else if (o instanceof char[]) {
                charArrayAppend(builder, ((char[]) o));
            } else if (o instanceof short[]) {
                shortArrayAppend(builder, ((short[]) o));
            } else if (o instanceof int[]) {
                intArrayAppend(builder, ((int[]) o));
            } else if (o instanceof long[]) {
                longArrayAppend(builder, ((long[]) o));
            } else if (o instanceof float[]) {
                floatArrayAppend(builder, ((float[]) o));
            } else if (o instanceof double[]) {
                doubleArrayAppend(builder, ((double[]) o));
            } else {
                objectArrayAppend(builder, ((Object[]) o), seenMap);
            }
        }
    }

    private static void safeObjectAppend(StringBuilder builder, Object o) {
        try {
            String oAsString = o.toString();
            builder.append(oAsString);
        } catch (Throwable var3) {
            builder.append("[FAILED toString()]");
        }
    }

    private static void objectArrayAppend(StringBuilder builder, Object[] a, Map<Object[], Object> seenMap) {
        builder.append("[");
        if (!seenMap.containsKey(a)) {
            seenMap.put(a, null);
            int len = a.length;
            for (int i = 0; i < len; ++i) {
                deeplyAppendParameter(builder, a[i], seenMap);
                if (i != len - 1) {
                    builder.append(", ");
                }
            }
            seenMap.remove(a);
        } else {
            builder.append("...");
        }
        builder.append("]");
    }

    private static void booleanArrayAppend(StringBuilder builder, boolean[] a) {
        builder.append("[");
        int len = a.length;
        for (int i = 0; i < len; ++i) {
            builder.append(a[i]);
            if (i != len - 1) {
                builder.append(", ");
            }
        }
        builder.append("]");
    }

    private static void byteArrayAppend(StringBuilder builder, byte[] a) {
        builder.append("[");
        int len = a.length;
        for (int i = 0; i < len; ++i) {
            builder.append(a[i]);
            if (i != len - 1) {
                builder.append(", ");
            }
        }
        builder.append("]");
    }

    private static void charArrayAppend(StringBuilder builder, char[] a) {
        builder.append("[");
        int len = a.length;
        for (int i = 0; i < len; ++i) {
            builder.append(a[i]);
            if (i != len - 1) {
                builder.append(", ");
            }
        }
        builder.append("]");
    }

    private static void shortArrayAppend(StringBuilder builder, short[] a) {
        builder.append("[");
        int len = a.length;
        for (int i = 0; i < len; ++i) {
            builder.append(a[i]);
            if (i != len - 1) {
                builder.append(", ");
            }
        }
        builder.append("]");
    }

    private static void intArrayAppend(StringBuilder builder, int[] a) {
        builder.append("[");
        int len = a.length;
        for (int i = 0; i < len; ++i) {
            builder.append(a[i]);
            if (i != len - 1) {
                builder.append(", ");
            }
        }
        builder.append("]");
    }

    private static void longArrayAppend(StringBuilder builder, long[] a) {
        builder.append("[");
        int len = a.length;
        for (int i = 0; i < len; ++i) {
            builder.append(a[i]);
            if (i != len - 1) {
                builder.append(", ");
            }
        }
        builder.append("]");
    }

    private static void floatArrayAppend(StringBuilder builder, float[] a) {
        builder.append("[");
        int len = a.length;
        for (int i = 0; i < len; ++i) {
            builder.append(a[i]);
            if (i != len - 1) {
                builder.append(", ");
            }
        }
        builder.append("]");
    }

    private static void doubleArrayAppend(StringBuilder builder, double[] a) {
        builder.append("[");
        int len = a.length;
        for (int i = 0; i < len; ++i) {
            builder.append(a[i]);
            if (i != len - 1) {
                builder.append(", ");
            }
        }
        builder.append("]");
    }
}
