package core.colin.basic.utils.netstate;

public class MaskConversion {

    public static String calcMaskByPrefixLength(int i) {
        int i2;
        int i3 = -1 << (32 - i);
        int[] iArr = new int[4];
        int i4 = 0;
        while (true) {
            if (i4 >= iArr.length) {
                break;
            }
            iArr[(iArr.length - 1) - i4] = (i3 >> (i4 * 8)) & 255;
            i4++;
        }
        String str = "" + iArr[0];
        for (i2 = 1; i2 < iArr.length; i2++) {
            str = str + "." + iArr[i2];
        }
        return str;
    }

    public static int calcPrefixLengthByMack(String str) {
        int indexOf;
        String[] split = str.split("\\.");
        int i = 0;
        for (int i2 = 0; i2 < split.length; i2++) {
            String stringBuffer = toBin(Integer.parseInt(split[i2])).reverse().toString();
//            Log.i("@@@", split[i2] + "---" + stringBuffer);
            int i3 = 0;
            int i4 = 0;
            while (i3 < stringBuffer.length() && (indexOf = stringBuffer.indexOf(49, i3)) != -1) {
                i4++;
                i3 = indexOf + 1;
            }
            i += i4;
        }
        return i;
    }

    public static StringBuffer toBin(int i) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(i % 2);
        for (int i2 = i / 2; i2 > 0; i2 /= 2) {
            stringBuffer.append(i2 % 2);
        }
        return stringBuffer;
    }
}
