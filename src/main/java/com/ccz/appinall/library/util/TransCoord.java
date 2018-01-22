package com.ccz.appinall.library.util;

public class TransCoord
{

    public TransCoord()
    {
    }

    public static CoordPoint getTransCoord(CoordPoint inPoint, int fromType, int toType)
    {
        return convertCoord(inPoint, fromType, toType, COORD_BASE[fromType][0], COORD_BASE[fromType][1], COORD_BASE[toType][0], COORD_BASE[toType][1]);
    }

    private static CoordPoint convertCoord(CoordPoint point, int fromType, int toType, double frombx, double fromby, double tobx, double toby)
    {
        CoordPoint transPt = null;
        double bx = frombx;
        switch(fromType)
        {
        case 9: // '\t'
        default:
            break;

        case 1: // '\001'
            if(frombx <= 0.0D)
            {
                bx = 127D;
                fromby = 38D;
            }
            transPt = convertTM2(point, toType, bx, fromby, tobx, toby);
            break;

        case 2: // '\002'
            if(frombx <= 0.0D)
            {
                bx = 128D;
                fromby = 38D;
            }
            transPt = convertKTM2(point, toType, tobx, toby);
            break;

        case 3: // '\003'
            if(frombx <= 0.0D)
            {
                bx = 129D;
                fromby = 0.0D;
            }
            transPt = convertUTM2(point, toType, bx, fromby, tobx, toby);
            break;

        case 4: // '\004'
            if(frombx <= 0.0D)
            {
                bx = 127D;
                fromby = 38D;
            }
            transPt = convertCONGNAMUL2(point, toType, bx, fromby, tobx, toby);
            break;

        case 5: // '\005'
            transPt = convertWGS2(point, toType, bx, fromby, tobx, toby);
            break;

        case 6: // '\006'
            transPt = convertBESSEL2(point, toType, bx, fromby, tobx, toby);
            break;

        case 7: // '\007'
            if(frombx <= 0.0D)
            {
                bx = 127D;
                fromby = 38D;
            }
            transPt = convertWTM2(point, toType, bx, fromby, tobx, toby);
            break;

        case 8: // '\b'
            if(frombx <= 0.0D)
            {
                bx = 128D;
                fromby = 38D;
            }
            transPt = convertWKTM2(point, toType, bx, frombx, tobx, toby);
            break;

        case 10: // '\n'
            if(frombx <= 0.0D)
            {
                bx = 127D;
                fromby = 38D;
            }
            transPt = convertWCONGNAMUL2(point, toType, bx, fromby, tobx, toby);
            break;
        }
        return transPt;
    }

    private static CoordPoint convertTM2(CoordPoint point, int toType, double frombx, double fromby, double tobx,
            double toby)
    {
        CoordPoint transPt = point.clone();
        switch(toType)
        {
        case 9: // '\t'
        default:
            break;

        case 1: // '\001'
            if(tobx <= 0.0D)
            {
                tobx = 127D;
                toby = 38D;
            }
            transPt.convertTM2BESSEL(frombx, fromby);
            transPt.convertBESSEL2TM(tobx, toby);
            break;

        case 2: // '\002'
            transPt.convertTM2BESSEL(frombx, fromby);
            transPt.convertBESSEL2KTM();
            break;

        case 3: // '\003'
            if(tobx <= 0.0D)
            {
                tobx = 129D;
                toby = 0.0D;
            }
            transPt.convertTM2BESSEL(frombx, fromby);
            transPt.convertBESSEL2WGS();
            transPt.convertWGS2UTM(tobx, toby);
            break;

        case 4: // '\004'
            transPt.convertTM2BESSEL(frombx, fromby);
            transPt.convertBESSEL2CONG();
            break;

        case 5: // '\005'
            transPt.convertTM2BESSEL(frombx, fromby);
            transPt.convertBESSEL2WGS();
            break;

        case 6: // '\006'
            transPt.convertTM2BESSEL(frombx, fromby);
            break;

        case 7: // '\007'
            if(tobx <= 0.0D)
            {
                tobx = 127D;
                toby = 38D;
            }
            transPt.convertTM2BESSEL(frombx, fromby);
            transPt.convertBESSEL2WGS();
            transPt.convertWGS2WTM(tobx, toby);
            break;

        case 8: // '\b'
            transPt.convertTM2BESSEL(frombx, fromby);
            transPt.convertBESSEL2WGS();
            transPt.convertWGS2WKTM();
            break;

        case 10: // '\n'
            transPt.convertTM2BESSEL(frombx, fromby);
            transPt.convertBESSEL2WGS();
            transPt.convertWGS2WCONG();
            break;
        }
        return transPt;
    }

    private static CoordPoint convertKTM2(CoordPoint point, int toType, double tobx, double toby)
    {
        CoordPoint transPt = point.clone();
        switch(toType)
        {
        case 2: // '\002'
        case 9: // '\t'
        default:
            break;

        case 1: // '\001'
            if(tobx <= 0.0D)
            {
                tobx = 127D;
                toby = 38D;
            }
            transPt.convertKTM2BESSEL();
            transPt.convertBESSEL2TM(tobx, toby);
            break;

        case 3: // '\003'
            if(tobx <= 0.0D)
            {
                tobx = 129D;
                toby = 0.0D;
            }
            transPt.convertKTM2BESSEL();
            transPt.convertBESSEL2WGS();
            transPt.convertWGS2UTM(tobx, toby);
            break;

        case 4: // '\004'
            transPt.convertKTM2BESSEL();
            transPt.convertBESSEL2CONG();
            break;

        case 5: // '\005'
            transPt.convertKTM2BESSEL();
            transPt.convertBESSEL2WGS();
            break;

        case 6: // '\006'
            transPt.convertKTM2BESSEL();
            break;

        case 7: // '\007'
            if(tobx <= 0.0D)
            {
                tobx = 127D;
                toby = 38D;
            }
            transPt.convertKTM2BESSEL();
            transPt.convertBESSEL2WGS();
            transPt.convertWGS2WTM(tobx, toby);
            break;

        case 8: // '\b'
            transPt.convertKTM2BESSEL();
            transPt.convertBESSEL2WGS();
            transPt.convertWGS2WKTM();
            break;

        case 10: // '\n'
            transPt.convertKTM2BESSEL();
            transPt.convertBESSEL2WGS();
            transPt.convertWGS2WCONG();
            break;
        }
        return transPt;
    }

    private static CoordPoint convertUTM2(CoordPoint point, int d, double e, double h, double g,
            double j)
    {
        CoordPoint transPt = point.clone();
        switch(d)
        {
        case 9: // '\t'
        default:
            break;

        case 1: // '\001'
            if(g <= 0.0D)
            {
                g = 127D;
                j = 38D;
            }
            transPt.convertUTM2WGS(e, h);
            transPt.convertWGS2BESSEL();
            transPt.convertBESSEL2TM(g, j);
            break;

        case 2: // '\002'
            transPt.convertUTM2WGS(e, h);
            transPt.convertWGS2BESSEL();
            transPt.convertBESSEL2KTM();
            break;

        case 3: // '\003'
            if(g <= 0.0D)
            {
                g = 129D;
                j = 0.0D;
            }
            transPt.convertUTM2WGS(e, h);
            transPt.convertWGS2UTM(g, j);
            break;

        case 4: // '\004'
            transPt.convertUTM2WGS(e, h);
            transPt.convertWGS2BESSEL();
            transPt.convertBESSEL2CONG();
            break;

        case 5: // '\005'
            transPt.convertUTM2WGS(e, h);
            break;

        case 6: // '\006'
            transPt.convertUTM2WGS(e, h);
            transPt.convertWGS2BESSEL();
            break;

        case 7: // '\007'
            if(g <= 0.0D)
            {
                g = 127D;
                j = 38D;
            }
            transPt.convertUTM2WGS(e, h);
            transPt.convertWGS2WTM(g, j);
            break;

        case 8: // '\b'
            transPt.convertUTM2WGS(e, h);
            transPt.convertWGS2WKTM();
            break;

        case 10: // '\n'
            transPt.convertUTM2WGS(e, h);
            transPt.convertWGS2WCONG();
            break;
        }
        return transPt;
    }

    private static CoordPoint convertCONGNAMUL2(CoordPoint point, int d, double e, double h, double g,
            double j)
    {
        CoordPoint transPt = point.clone();
        switch(d)
        {
        case 4: // '\004'
        case 9: // '\t'
        default:
            break;

        case 1: // '\001'
            if(g <= 0.0D)
            {
                g = 127D;
                j = 38D;
            }
            transPt.convertCONG2BESSEL();
            transPt.convertBESSEL2TM(g, j);
            break;

        case 2: // '\002'
            transPt.convertCONG2BESSEL();
            transPt.convertBESSEL2KTM();
            break;

        case 3: // '\003'
            if(g <= 0.0D)
            {
                g = 129D;
                j = 0.0D;
            }
            transPt.convertCONG2BESSEL();
            transPt.convertBESSEL2WGS();
            transPt.convertWGS2UTM(g, j);
            break;

        case 5: // '\005'
            transPt.convertCONG2BESSEL();
            transPt.convertBESSEL2WGS();
            break;

        case 6: // '\006'
            transPt.convertCONG2BESSEL();
            break;

        case 7: // '\007'
            if(g <= 0.0D)
            {
                g = 127D;
                j = 38D;
            }
            transPt.convertCONG2BESSEL();
            transPt.convertBESSEL2WGS();
            transPt.convertWGS2WTM(g, j);
            break;

        case 8: // '\b'
            transPt.convertCONG2BESSEL();
            transPt.convertBESSEL2WGS();
            transPt.convertWGS2WKTM();
            break;

        case 10: // '\n'
            transPt.convertCONG2BESSEL();
            transPt.convertBESSEL2WGS();
            transPt.convertWGS2WCONG();
            break;
        }
        return transPt;
    }

    private static CoordPoint convertWGS2(CoordPoint point, int d, double e, double h, double g,
            double j)
    {
        CoordPoint transPt = point.clone();
        switch(d)
        {
        case 5: // '\005'
        case 9: // '\t'
        default:
            break;

        case 1: // '\001'
            if(g <= 0.0D)
            {
                g = 127D;
                j = 38D;
            }
            transPt.convertWGS2BESSEL();
            transPt.convertBESSEL2TM(g, j);
            break;

        case 2: // '\002'
            transPt.convertWGS2BESSEL();
            transPt.convertBESSEL2KTM();
            break;

        case 3: // '\003'
            if(g <= 0.0D)
            {
                g = 129D;
                j = 0.0D;
            }
            transPt.convertWGS2UTM(g, j);
            break;

        case 4: // '\004'
            transPt.convertWGS2BESSEL();
            transPt.convertBESSEL2CONG();
            break;

        case 6: // '\006'
            transPt.convertWGS2BESSEL();
            break;

        case 7: // '\007'
            if(g <= 0.0D)
            {
                g = 127D;
                j = 38D;
            }
            transPt.convertWGS2WTM(g, j);
            break;

        case 8: // '\b'
            transPt.convertWGS2WKTM();
            break;

        case 10: // '\n'
            transPt.convertWGS2WCONG();
            break;
        }
        return transPt;
    }

    private static CoordPoint convertBESSEL2(CoordPoint point, int d, double e, double h, double g,
            double j)
    {
        CoordPoint transPt = point.clone();
        switch(d)
        {
        case 6: // '\006'
        case 9: // '\t'
        default:
            break;

        case 1: // '\001'
            if(g <= 0.0D)
            {
                g = 127D;
                j = 38D;
            }
            transPt.convertBESSEL2TM(g, j);
            break;

        case 2: // '\002'
            transPt.convertBESSEL2KTM();
            break;

        case 3: // '\003'
            if(g <= 0.0D)
            {
                g = 129D;
                j = 0.0D;
            }
            transPt.convertBESSEL2WGS();
            transPt.convertWGS2UTM(g, j);
            break;

        case 4: // '\004'
            transPt.convertBESSEL2CONG();
            break;

        case 5: // '\005'
            transPt.convertBESSEL2WGS();
            break;

        case 7: // '\007'
            if(g <= 0.0D)
            {
                g = 127D;
                j = 38D;
            }
            transPt.convertBESSEL2WGS();
            transPt.convertWGS2WTM(g, j);
            break;

        case 8: // '\b'
            transPt.convertBESSEL2WGS();
            transPt.convertWGS2WKTM();
            break;

        case 10: // '\n'
            transPt.convertBESSEL2WGS();
            transPt.convertWGS2WCONG();
            break;
        }
        return transPt;
    }

    private static CoordPoint convertWTM2(CoordPoint point, int d, double e, double h, double g,
            double j)
    {
        CoordPoint transPt = point.clone();
        switch(d)
        {
        case 9: // '\t'
        default:
            break;

        case 1: // '\001'
            if(g <= 0.0D)
            {
                g = 127D;
                j = 38D;
            }
            transPt.convertWTM2WGS(e, h);
            transPt.convertWGS2BESSEL();
            transPt.convertBESSEL2TM(g, j);
            break;

        case 2: // '\002'
            transPt.convertWTM2WGS(e, h);
            transPt.convertWGS2BESSEL();
            transPt.convertBESSEL2KTM();
            break;

        case 3: // '\003'
            if(g <= 0.0D)
            {
                g = 129D;
                j = 0.0D;
            }
            transPt.convertWTM2WGS(e, h);
            transPt.convertWGS2UTM(g, j);
            break;

        case 4: // '\004'
            transPt.convertWTM2WGS(e, h);
            transPt.convertWGS2BESSEL();
            transPt.convertBESSEL2CONG();
            break;

        case 5: // '\005'
            transPt.convertWTM2WGS(e, h);
            transPt.convertWGS2BESSEL();
            transPt.convertBESSEL2WGS();
            break;

        case 6: // '\006'
            transPt.convertWTM2WGS(e, h);
            transPt.convertWGS2BESSEL();
            break;

        case 7: // '\007'
            if(g <= 0.0D)
            {
                g = 127D;
                j = 38D;
            }
            transPt.convertWTM2WGS(e, h);
            transPt.convertWGS2WTM(g, j);
            break;

        case 8: // '\b'
            transPt.convertWTM2WGS(e, h);
            transPt.convertWGS2WKTM();
            break;

        case 10: // '\n'
            transPt.convertWTM2WGS(e, h);
            transPt.convertWGS2WCONG();
            break;
        }
        return transPt;
    }

    private static CoordPoint convertWKTM2(CoordPoint point, int d, double e, double h, double g,
            double j)
    {
        CoordPoint transPt = point.clone();
        switch(d)
        {
        case 2: // '\002'
        case 9: // '\t'
        default:
            break;

        case 1: // '\001'
            if(g <= 0.0D)
            {
                g = 127D;
                j = 38D;
            }
            transPt.convertWKTM2WGS();
            transPt.convertWGS2BESSEL();
            transPt.convertBESSEL2TM(g, j);
            break;

        case 3: // '\003'
            if(g <= 0.0D)
            {
                g = 129D;
                j = 0.0D;
            }
            transPt.convertWKTM2WGS();
            transPt.convertWGS2UTM(g, j);
            break;

        case 4: // '\004'
            transPt.convertWKTM2WGS();
            transPt.convertWGS2BESSEL();
            transPt.convertBESSEL2CONG();
            break;

        case 5: // '\005'
            transPt.convertWKTM2WGS();
            transPt.convertWGS2BESSEL();
            transPt.convertBESSEL2WGS();
            break;

        case 6: // '\006'
            transPt.convertWKTM2WGS();
            transPt.convertWGS2BESSEL();
            break;

        case 7: // '\007'
            if(g <= 0.0D)
            {
                g = 127D;
                j = 38D;
            }
            transPt.convertWKTM2WGS();
            transPt.convertWGS2WTM(g, j);
            break;

        case 8: // '\b'
            transPt.convertWKTM2WGS();
            transPt.convertWGS2WKTM();
            break;

        case 10: // '\n'
            transPt.convertWKTM2WGS();
            transPt.convertWGS2WCONG();
            break;
        }
        return transPt;
    }

    private static CoordPoint convertWCONGNAMUL2(CoordPoint point, int d, double e, double h, double g,
            double j)
    {
        CoordPoint transPt = point.clone();
        switch(d)
        {
        case 9: // '\t'
        case 10: // '\n'
        default:
            break;

        case 1: // '\001'
            if(g <= 0.0D)
            {
                g = 127D;
                j = 38D;
            }
            transPt.convertWCONG2WGS();
            transPt.convertWGS2BESSEL();
            transPt.convertBESSEL2TM(g, j);
            break;

        case 2: // '\002'
            transPt.convertWCONG2WGS();
            transPt.convertWGS2BESSEL();
            transPt.convertBESSEL2KTM();
            break;

        case 3: // '\003'
            if(g <= 0.0D)
            {
                g = 129D;
                j = 0.0D;
            }
            transPt.convertWCONG2WGS();
            transPt.convertWGS2UTM(g, j);
            break;

        case 4: // '\004'
            transPt.convertWCONG2WGS();
            transPt.convertWGS2BESSEL();
            transPt.convertBESSEL2CONG();
            break;

        case 5: // '\005'
            transPt.convertWCONG2WGS();
            transPt.convertWGS2BESSEL();
            transPt.convertBESSEL2WGS();
            break;

        case 6: // '\006'
            transPt.convertWCONG2WGS();
            transPt.convertWGS2BESSEL();
            break;

        case 7: // '\007'
            if(g <= 0.0D)
            {
                g = 127D;
                j = 38D;
            }
            transPt.convertWCONG2WGS();
            transPt.convertWGS2WTM(g, j);
            break;

        case 8: // '\b'
            transPt.convertWCONG2WGS();
            transPt.convertWGS2WKTM();
            break;
        }
        return transPt;
    }

    public static final int COORD_TYPE_TM = 1;
    public static final int COORD_TYPE_KTM = 2;
    public static final int COORD_TYPE_UTM = 3;
    public static final int COORD_TYPE_CONGNAMUL = 4;
    public static final int COORD_TYPE_WGS84 = 5;
    public static final int COORD_TYPE_BESSEL = 6;
    public static final int COORD_TYPE_WTM = 7;
    public static final int COORD_TYPE_WKTM = 8;
    public static final int COORD_TYPE_WCONGNAMUL = 10;
    public static final double BASE_TM_LON = 127D;
    public static final double BASE_TM_LAT = 38D;
    public static final double BASE_KTM_LON = 128D;
    public static final double BASE_KTM_LAT = 38D;
    public static final double BASE_UTM_LON = 129D;
    public static final double BASE_UTM_LAT = 0D;
    private static final int COORD_BASE[][] = {
        new int[2], {
            127, 38
        }, {
            -1, -1
        }, {
            129, 0
        }, {
            -1, -1
        }, {
            -1, -1
        }, {
            -1, -1
        }, {
            127, 38
        }, {
            -1, -1
        }, new int[2],
        {
            -1, -1
        }
    };

}