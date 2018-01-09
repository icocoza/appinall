package com.ccz.appinall.library.util;

public class HttpUtil {
	public static int httpHeaderSplitPos(byte[] buf, int offset, long len)
    {
        int index = 0;
        while (index < len)
        {
            if (buf[index + offset] == '\r' && buf[++index + offset] == '\n' && buf[++index + offset] == '\r' && buf[++index + offset] == '\n')
                return ++index;
            index++;
        }
        return -1;
    }
	
    public static String decodePercent(String data)
    {
        try
        {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < data.length(); i++)
            {
                char ch = data.charAt(i);
                switch (ch)
                {
                    case '+':
                        sb.append(' ');
                        break;
                    case '%':
                        sb.append((char)Integer.parseInt(data.substring(i + 1, i + 3)));
                        i += 2;
                        break;
                    default:
                        sb.append(ch);
                        break;
                }
            }
            return sb.toString();
        }
        catch(Exception e)
        {
        }
        return data;
    }
}
