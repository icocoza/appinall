package com.ccz.appinall.library.util;

public class CoordPoint
{
    private class CoordRect
    {

        public double x;
        public double y;
        public double w;
        public double h;

        public CoordRect(double x, double y, double w,
                double h)
        {
            this.x = x;
            this.y = y;
            this.w = w;
            this.h = h;
        }
    }


    public CoordPoint()
    {
        m_imode = 0.0D;
        m_ds = 0.0D;
        m_kappa = 0.0D;
        m_phi = 0.0D;
        m_omega = 0.0D;
        m_dz = 0.0D;
        m_dy = 0.0D;
        m_dx = 0.0D;
        for(int i = 0; i < rectArray2.length; i++)
        {
            rectArray2[i].x += deltaValue1[i][0];
            rectArray2[i].y += deltaValue1[i][1];
        }

    }

    public CoordPoint(double x, double y)
    {
        m_imode = 0.0D;
        m_ds = 0.0D;
        m_kappa = 0.0D;
        m_phi = 0.0D;
        m_omega = 0.0D;
        m_dz = 0.0D;
        m_dy = 0.0D;
        m_dx = 0.0D;
        this.x = x;
        this.y = y;
    }

    public CoordPoint clone()
    {
        return new CoordPoint(x, y);
    }

    public void convertBESSEL2KTM()
    {
        GP2TM(6377397.1550000003D, 0.0033427731799399794D, 600000D, 400000D, 0.99990000000000001D, 38D, 128D);
    }

    public void convertBESSEL2CONG()
    {
        GP2TM(6377397.1550000003D, 0.0033427731799399794D, 500000D, 200000D, 1.0D, 38D, 127.00289027777778D);
        shiftIsland(true);
    }

    public void convertBESSEL2WGS()
    {
        setParameter(115.8D, -474.99000000000001D, -674.11000000000001D, 1.1599999999999999D, -2.3100000000000001D, -1.6299999999999999D, -6.4299999999999997D, 1.0D);
        double rtn[] = GP2WGP(y, x, 0.0D, 6377397.1550000003D, 0.0033427731799399794D);
        x = rtn[1];
        y = rtn[0];
    }

    public void convertKTM2BESSEL()
    {
        TM2GP(6377397.1550000003D, 0.0033427731799399794D, 600000D, 400000D, 0.99990000000000001D, 38D, 128D);
    }

    public void convertBESSEL2TM(double d, double e)
    {
        GP2TM(6377397.1550000003D, 0.0033427731799399794D, 500000D, 200000D, 1.0D, e, d + 0.0028902777777777776D);
    }

    public void convertTM2BESSEL(double d, double e)
    {
        TM2GP(6377397.1550000003D, 0.0033427731799399794D, 500000D, 200000D, 1.0D, e, d + 0.0028902777777777776D);
    }

    public void convertWGS2UTM(double d, double e)
    {
        setParameter(115.8D, -474.99000000000001D, -674.11000000000001D, 1.1599999999999999D, -2.3100000000000001D, -1.6299999999999999D, -6.4299999999999997D, 1.0D);
        GP2TM(6378137D, 0.0033528106647474805D, 0.0D, 500000D, 0.99960000000000004D, e, d);
    }

    public void convertWGS2WTM(double d, double e)
    {
        GP2TM(6378137D, 0.0033528106647474805D, 500000D, 200000D, 1.0D, e, d);
    }

    public void convertWGS2WKTM()
    {
        GP2TM(6378137D, 0.0033528106647474805D, 600000D, 400000D, 0.99990000000000001D, 38D, 128D);
    }

    public void convertWGS2WCONG()
    {
        GP2TM(6378137D, 0.0033528106647474805D, 500000D, 200000D, 1.0D, 38D, 127D);
        x = Math.round(x * 2.5D);
        y = Math.round(y * 2.5D);
    }

    public void convertUTM2WGS(double d, double e)
    {
        setParameter(115.8D, -474.99000000000001D, -674.11000000000001D, 1.1599999999999999D, -2.3100000000000001D, -1.6299999999999999D, -6.4299999999999997D, 1.0D);
        TM2GP(6378137D, 0.0033528106647474805D, 0.0D, 500000D, 0.99960000000000004D, e, d);
    }

    public void convertWGS2BESSEL()
    {
        setParameter(115.8D, -474.99000000000001D, -674.11000000000001D, 1.1599999999999999D, -2.3100000000000001D, -1.6299999999999999D, -6.4299999999999997D, 1.0D);
        double rtn[] = WGP2GP(y, x, 0.0D, 6377397.1550000003D, 0.0033427731799399794D);
        x = rtn[1];
        y = rtn[0];
    }

    public void convertCONG2BESSEL()
    {
        shiftIsland(false);
        TM2GP(6377397.1550000003D, 0.0033427731799399794D, 500000D, 200000D, 1.0D, 38D, 127.00289027777778D);
    }

    public void convertWTM2WGS(double d, double e)
    {
        TM2GP(6378137D, 0.0033528106647474805D, 500000D, 200000D, 1.0D, e, d);
    }

    public void convertWKTM2WGS()
    {
        TM2GP(6378137D, 0.0033528106647474805D, 600000D, 400000D, 0.99990000000000001D, 38D, 128D);
    }

    public void convertWCONG2WGS()
    {
        x /= 2.5D;
        y /= 2.5D;
        TM2GP(6378137D, 0.0033528106647474805D, 500000D, 200000D, 1.0D, 38D, 127D);
    }

    private double[] WGP2GP(double a, double b, double d, double e, double h)
    {
        double rtn[] = WGP2WCTR(a, b, d);
        if(m_imode == 1.0D)
            rtn = TransMolod(rtn[0], rtn[1], rtn[2]);
        else
            rtn = TransBursa(rtn[0], rtn[1], rtn[2]);
        return CTR2GP(rtn[0], rtn[1], rtn[2], e, h);
    }

    private double[] WGP2WCTR(double a, double b, double d)
    {
        return GP2CTR(a, b, d, 6378137D, 0.0033528106647474805D);
    }

    private double[] GP2WGP(double a, double b, double d, double e, double h)
    {
        double rtn[] = GP2CTR(a, b, d, e, h);
        if(m_imode == 1.0D)
            rtn = InverseMolod(rtn[0], rtn[1], rtn[2]);
        else
            rtn = InverseBursa(rtn[0], rtn[1], rtn[2]);
        return WCTR2WGP(rtn[0], rtn[1], rtn[2]);
    }

    private double[] GP2CTR(double a, double b, double d, double e, double h)
    {
        double rtn[] = new double[3];
        double j = 0.0D;
        double l = 0.0D;
        double o = 0.0D;
        double m = h;
        if(m > 1.0D)
            m = 1.0D / m;
        j = Math.atan(1.0D) / 45D;
        l = a * j;
        j = b * j;
        m = 1.0D / m;
        m = (e * (m - 1.0D)) / m;
        o = (Math.pow(e, 2D) - Math.pow(m, 2D)) / Math.pow(e, 2D);
        o = e / Math.sqrt(1.0D - o * Math.pow(Math.sin(l), 2D));
        rtn[0] = (o + d) * Math.cos(l) * Math.cos(j);
        rtn[1] = (o + d) * Math.cos(l) * Math.sin(j);
        rtn[2] = ((Math.pow(m, 2D) / Math.pow(e, 2D)) * o + d) * Math.sin(l);
        return rtn;
    }

    private double[] InverseMolod(double a, double b, double d)
    {
        double rtn[] = new double[3];
        double e = 0.0D;
        double h = 0.0D;
        double g = 0.0D;
        e = (a - m_dx) * (1.0D + m_ds);
        h = (b - m_dy) * (1.0D + m_ds);
        g = (d - m_dz) * (1.0D + m_ds);
        rtn[0] = (1.0D / (1.0D + m_ds)) * ((e - m_kappa * h) + m_phi * g);
        rtn[1] = (1.0D / (1.0D + m_ds)) * ((m_kappa * e + h) - m_omega * g);
        rtn[2] = (1.0D / (1.0D + m_ds)) * (-1D * m_phi * e + m_omega * h + g);
        return rtn;
    }

    private double[] InverseBursa(double a, double b, double d)
    {
        double e = a - m_dx;
        double h = b - m_dy;
        double g = d - m_dz;
        double rtn[] = new double[3];
        rtn[0] = (1.0D / (1.0D + m_ds)) * ((e - m_kappa * h) + m_phi * g);
        rtn[1] = (1.0D / (1.0D + m_ds)) * ((m_kappa * e + h) - m_omega * g);
        rtn[2] = (1.0D / (1.0D + m_ds)) * (-1D * m_phi * e + m_omega * h + g);
        return rtn;
    }

    private double[] TransMolod(double a, double b, double d)
    {
        double rtn[] = new double[3];
        rtn[0] = a + (1.0D + m_ds) * (m_kappa * b - m_phi * d) + m_dx;
        rtn[1] = b + (1.0D + m_ds) * (-1D * m_kappa * a + m_omega * d) + m_dy;
        rtn[2] = d + (1.0D + m_ds) * (m_phi * a - m_omega * b) + m_dz;
        return rtn;
    }

    private double[] TransBursa(double a, double b, double d)
    {
        double rtn[] = new double[3];
        rtn[0] = (1.0D + m_ds) * ((a + m_kappa * b) - m_phi * d) + m_dx;
        rtn[1] = (1.0D + m_ds) * (-1D * m_kappa * a + b + m_omega * d) + m_dy;
        rtn[2] = (1.0D + m_ds) * ((m_phi * a - m_omega * b) + d) + m_dz;
        return rtn;
    }

    private double[] WCTR2WGP(double a, double b, double d)
    {
        return CTR2GP(a, b, d, 6378137D, 0.0033528106647474805D);
    }

    private double[] CTR2GP(double a, double b, double d, double e, double h)
    {
        double m = h;
        double w = 0.0D;
        double g = 0.0D;
        double o = 0.0D;
        double D = 0.0D;
        double A = 0.0D;
        double u = 0.0D;
        double l = 0.0D;
        double j = 0.0D;
        if(m > 1.0D)
            m = 1.0D / m;
        g = Math.atan(1.0D) / 45D;
        m = 1.0D / m;
        o = (e * (m - 1.0D)) / m;
        D = (Math.pow(e, 2D) - Math.pow(o, 2D)) / Math.pow(e, 2D);
        m = Math.atan(b / a);
        A = Math.sqrt(a * a + b * b);
        u = e;
        b = 0.0D;
        do
        {
            b++;
            w = Math.pow((Math.pow(o, 2D) / Math.pow(e, 2D)) * u + w, 2D) - Math.pow(d, 2D);
            w = d / Math.sqrt(w);
            l = Math.atan(w);
            if(Math.abs(l - j) < 1.0000000000000001E-018D)
                break;
            u = e / Math.sqrt(1.0D - D * Math.pow(Math.sin(l), 2D));
            w = A / Math.cos(l) - u;
            j = l;
        } while(b <= 30D);
        double rtn[] = new double[2];
        rtn[0] = l / g;
        rtn[1] = m / g;
        if(a < 0.0D)
            rtn[1] = 180D + rtn[1];
        if(rtn[1] < 0.0D)
            rtn[1] = 360D + rtn[1];
        return rtn;
    }

    private void GP2TM(double d, double e, double h, double g, double j, double l, double m)
    {
        double a = y;
        double b = x;
        double w = e;
        double A = 0.0D;
        double o = 0.0D;
        double D = 0.0D;
        double u = 0.0D;
        double z = 0.0D;
        double G = 0.0D;
        double E = 0.0D;
        double I = 0.0D;
        double J = 0.0D;
        double L = 0.0D;
        double M = 0.0D;
        double H = 0.0D;
        double B = g;
        if(w > 1.0D)
            w = 1.0D / w;
        A = Math.atan(1.0D) / 45D;
        o = a * A;
        D = b * A;
        u = l * A;
        A = m * A;
        w = 1.0D / w;
        z = (d * (w - 1.0D)) / w;
        G = (Math.pow(d, 2D) - Math.pow(z, 2D)) / Math.pow(d, 2D);
        w = (Math.pow(d, 2D) - Math.pow(z, 2D)) / Math.pow(z, 2D);
        z = (d - z) / (d + z);
        E = d * ((1.0D - z) + (5D * (Math.pow(z, 2D) - Math.pow(z, 3D))) / 4D + (81D * (Math.pow(z, 4D) - Math.pow(z, 5D))) / 64D);
        I = (3D * d * ((z - Math.pow(z, 2D)) + (7D * (Math.pow(z, 3D) - Math.pow(z, 4D))) / 8D + (55D * Math.pow(z, 5D)) / 64D)) / 2D;
        J = (15D * d * ((Math.pow(z, 2D) - Math.pow(z, 3D)) + (3D * (Math.pow(z, 4D) - Math.pow(z, 5D))) / 4D)) / 16D;
        L = (35D * d * ((Math.pow(z, 3D) - Math.pow(z, 4D)) + (11D * Math.pow(z, 5D)) / 16D)) / 48D;
        M = (315D * d * (Math.pow(z, 4D) - Math.pow(z, 5D))) / 512D;
        D -= A;
        u = (((E * u - I * Math.sin(2D * u)) + J * Math.sin(4D * u)) - L * Math.sin(6D * u)) + M * Math.sin(8D * u);
        z = u * j;
        H = Math.sin(o);
        u = Math.cos(o);
        A = H / u;
        w *= Math.pow(u, 2D);
        G = d / Math.sqrt(1.0D - G * Math.pow(Math.sin(o), 2D));
        o = (((E * o - I * Math.sin(2D * o)) + J * Math.sin(4D * o)) - L * Math.sin(6D * o)) + M * Math.sin(8D * o);
        o *= j;
        E = (G * H * u * j) / 2D;
        I = (G * H * Math.pow(u, 3D) * j * ((5D - Math.pow(A, 2D)) + 9D * w + 4D * Math.pow(w, 2D))) / 24D;
        J = (G * H * Math.pow(u, 5D) * j * (((((((61D - 58D * Math.pow(A, 2D)) + Math.pow(A, 4D) + 270D * w) - 330D * Math.pow(A, 2D) * w) + 445D * Math.pow(w, 2D) + 324D * Math.pow(w, 3D)) - 680D * Math.pow(A, 2D) * Math.pow(w, 2D)) + 88D * Math.pow(w, 4D)) - 600D * Math.pow(A, 2D) * Math.pow(w, 3D) - 192D * Math.pow(A, 2D) * Math.pow(w, 4D))) / 720D;
        H = (G * H * Math.pow(u, 7D) * j * (((1385D - 3111D * Math.pow(A, 2D)) + 543D * Math.pow(A, 4D)) - Math.pow(A, 6D))) / 40320D;
        o = o + Math.pow(D, 2D) * E + Math.pow(D, 4D) * I + Math.pow(D, 6D) * J + Math.pow(D, 8D) * H;
        y = (o - z) + h;
        o = G * u * j;
        z = (G * Math.pow(u, 3D) * j * ((1.0D - Math.pow(A, 2D)) + w)) / 6D;
        w = (G * Math.pow(u, 5D) * j * (((((5D - 18D * Math.pow(A, 2D)) + Math.pow(A, 4D) + 14D * w) - 58D * Math.pow(A, 2D) * w) + 13D * Math.pow(w, 2D) + 4D * Math.pow(w, 3D)) - 64D * Math.pow(A, 2D) * Math.pow(w, 2D) - 25D * Math.pow(A, 2D) * Math.pow(w, 3D))) / 120D;
        u = (G * Math.pow(u, 7D) * j * (((61D - 479D * Math.pow(A, 2D)) + 179D * Math.pow(A, 4D)) - Math.pow(A, 6D))) / 5040D;
        x = B + D * o + Math.pow(D, 3D) * z + Math.pow(D, 5D) * w + Math.pow(D, 7D) * u;
    }

    private void TM2GP(double d, double e, double h, double g, double j, double l, double m)
    {
        double u = e;
        double A = 0.0D;
        double w = 0.0D;
        double o = 0.0D;
        double D = 0.0D;
        double B = 0.0D;
        double z = 0.0D;
        double G = 0.0D;
        double E = 0.0D;
        double I = 0.0D;
        double J = 0.0D;
        double L = 0.0D;
        double M = 0.0D;
        double H = 0.0D;
        double a = y;
        double b = x;
        if(u > 1.0D)
            u = 1.0D / u;
        A = g;
        w = Math.atan(1.0D) / 45D;
        o = l * w;
        D = m * w;
        u = 1.0D / u;
        B = (d * (u - 1.0D)) / u;
        z = (Math.pow(d, 2D) - Math.pow(B, 2D)) / Math.pow(d, 2D);
        u = (Math.pow(d, 2D) - Math.pow(B, 2D)) / Math.pow(B, 2D);
        B = (d - B) / (d + B);
        G = d * ((1.0D - B) + (5D * (Math.pow(B, 2D) - Math.pow(B, 3D))) / 4D + (81D * (Math.pow(B, 4D) - Math.pow(B, 5D))) / 64D);
        E = (3D * d * ((B - Math.pow(B, 2D)) + (7D * (Math.pow(B, 3D) - Math.pow(B, 4D))) / 8D + (55D * Math.pow(B, 5D)) / 64D)) / 2D;
        I = (15D * d * ((Math.pow(B, 2D) - Math.pow(B, 3D)) + (3D * (Math.pow(B, 4D) - Math.pow(B, 5D))) / 4D)) / 16D;
        J = (35D * d * ((Math.pow(B, 3D) - Math.pow(B, 4D)) + (11D * Math.pow(B, 5D)) / 16D)) / 48D;
        L = (315D * d * (Math.pow(B, 4D) - Math.pow(B, 5D))) / 512D;
        o = (((G * o - E * Math.sin(2D * o)) + I * Math.sin(4D * o)) - J * Math.sin(6D * o)) + L * Math.sin(8D * o);
        o *= j;
        o = (a + o) - h;
        M = o / j;
        H = (d * (1.0D - z)) / Math.pow(Math.sqrt(1.0D - z * Math.pow(Math.sin(0.0D), 2D)), 3D);
        o = M / H;
        for(a = 1.0D; a <= 5D; a++)
        {
            B = (((G * o - E * Math.sin(2D * o)) + I * Math.sin(4D * o)) - J * Math.sin(6D * o)) + L * Math.sin(8D * o);
            H = (d * (1.0D - z)) / Math.pow(Math.sqrt(1.0D - z * Math.pow(Math.sin(o), 2D)), 3D);
            o += (M - B) / H;
        }

        H = (d * (1.0D - z)) / Math.pow(Math.sqrt(1.0D - z * Math.pow(Math.sin(o), 2D)), 3D);
        G = d / Math.sqrt(1.0D - z * Math.pow(Math.sin(o), 2D));
        B = Math.sin(o);
        z = Math.cos(o);
        E = B / z;
        u *= Math.pow(z, 2D);
        A = b - A;
        B = E / (2D * H * G * Math.pow(j, 2D));
        I = (E * ((5D + 3D * Math.pow(E, 2D) + u) - 4D * Math.pow(u, 2D) - 9D * Math.pow(E, 2D) * u)) / (24D * H * Math.pow(G, 3D) * Math.pow(j, 4D));
        J = (E * ((((((61D + 90D * Math.pow(E, 2D) + 46D * u + 45D * Math.pow(E, 4D)) - 252D * Math.pow(E, 2D) * u - 3D * Math.pow(u, 2D)) + 100D * Math.pow(u, 3D)) - 66D * Math.pow(E, 2D) * Math.pow(u, 2D) - 90D * Math.pow(E, 4D) * u) + 88D * Math.pow(u, 4D) + 225D * Math.pow(E, 4D) * Math.pow(u, 2D) + 84D * Math.pow(E, 2D) * Math.pow(u, 3D)) - 192D * Math.pow(E, 2D) * Math.pow(u, 4D))) / (720D * H * Math.pow(G, 5D) * Math.pow(j, 6D));
        H = (E * (1385D + 3633D * Math.pow(E, 2D) + 4095D * Math.pow(E, 4D) + 1575D * Math.pow(E, 6D))) / (40320D * H * Math.pow(G, 7D) * Math.pow(j, 8D));
        o = (((o - Math.pow(A, 2D) * B) + Math.pow(A, 4D) * I) - Math.pow(A, 6D) * J) + Math.pow(A, 8D) * H;
        B = 1.0D / (G * z * j);
        H = (1.0D + 2D * Math.pow(E, 2D) + u) / (6D * Math.pow(G, 3D) * z * Math.pow(j, 3D));
        u = (((((5D + 6D * u + 28D * Math.pow(E, 2D)) - 3D * Math.pow(u, 2D)) + 8D * Math.pow(E, 2D) * u + 24D * Math.pow(E, 4D)) - 4D * Math.pow(u, 3D)) + 4D * Math.pow(E, 2D) * Math.pow(u, 2D) + 24D * Math.pow(E, 2D) * Math.pow(u, 3D)) / (120D * Math.pow(G, 5D) * z * Math.pow(j, 5D));
        z = (61D + 662D * Math.pow(E, 2D) + 1320D * Math.pow(E, 4D) + 720D * Math.pow(E, 6D)) / (5040D * Math.pow(G, 7D) * z * Math.pow(j, 7D));
        A = ((A * B - Math.pow(A, 3D) * H) + Math.pow(A, 5D) * u) - Math.pow(A, 7D) * z;
        D += A;
        x = D / w;
        y = o / w;
    }

    private void setParameter(double a, double b, double d, double e, double h, double g, double j,
            double l)
    {
        double m = Math.atan(1.0D) / 45D;
        m_dx = a;
        m_dy = b;
        m_dz = d;
        m_omega = (e / 3600D) * m;
        m_phi = (h / 3600D) * m;
        m_kappa = (g / 3600D) * m;
        m_ds = j * 9.9999999999999995E-007D;
        m_imode = l;
    }

    private void shiftIsland(boolean d)
    {
        double e = 0.0D;
        double h = 0.0D;
        double x;
        double y;
        if(d)
        {
            for(int i = 0; i < rectArray1.length; i++)
            {
                if(this.x - rectArray1[i].x < 0.0D || this.x - rectArray1[i].x > rectArray1[i].w || this.y - rectArray1[i].y < 0.0D || this.y - rectArray1[i].y > rectArray1[i].h)
                    continue;
                e += deltaValue1[i][0];
                h += deltaValue1[i][1];
                break;
            }

            x = (int)((this.x + e) * 2.5D + 0.5D);
            y = (int)((this.y + h) * 2.5D + 0.5D);
        } else
        {
            x = this.x / 2.5D;
            y = this.y / 2.5D;
            for(int i = 0; i < rectArray2.length; i++)
            {
                if(x - rectArray2[i].x < 0.0D || x - rectArray2[i].x > rectArray2[i].w || y - rectArray2[i].y < 0.0D || y - rectArray2[i].y > rectArray2[i].h)
                    continue;
                x += deltaValue2[i][0];
                y += deltaValue2[i][1];
                break;
            }

        }
        this.x = x;
        this.y = y;
    }

    public double x;
    public double y;
    private double m_imode;
    private double m_ds;
    private double m_kappa;
    private double m_phi;
    private double m_omega;
    private double m_dz;
    private double m_dy;
    private double m_dx;
/*    private static final double m_AW = 6378137D;
    private static final double m_FW = 0.0033528106647474805D;
    private static final double m_AB = 6377397.1550000003D;
    private static final double m_FB = 0.0033427731799399794D;
    private static final double m_OKKTM = 1D;
    private static final double m_OKUTM = 0.99960000000000004D;
    private static final double m_OKGTM = 0.99990000000000001D;
    private static final double m_TX = 115.8D;
    private static final double m_TY = -474.99000000000001D;
    private static final double m_TZ = -674.11000000000001D;
    private static final double m_TOMEGA = 1.1599999999999999D;
    private static final double m_TPHI = -2.3100000000000001D;
    private static final double m_TKAPPA = -1.6299999999999999D;
    private static final double m_TS = -6.4299999999999997D;
    private static final double m_TMODE = 1D;
    private static final double m_ux0 = 0D;
    private static final double m_uy0 = 500000D;
    private static final double m_x0 = 500000D;
    private static final double m_y0 = 200000D;
    private static final double m_x1 = 600000D;
    private static final double m_y1 = 400000D;*/
    private final CoordRect rectArray1[] = {
        new CoordRect(112500D, -50000D, 33500D, 53000D), new CoordRect(146000D, -50000D, 54000D, 58600D), new CoordRect(130000D, 44000D, 15000D, 14000D), new CoordRect(532500D, 437500D, 25000D, 25000D), new CoordRect(625000D, 412500D, 25000D, 25000D), new CoordRect(-12500D, 462500D, 17500D, 50000D)
    };
    private final CoordRect rectArray2[] = {
        new CoordRect(112500D, -50000D, 33500D, 53000D), new CoordRect(146000D, -50000D, 54000D, 58600D), new CoordRect(130000D, 44000D, 15000D, 14000D), new CoordRect(532500D, 437500D, 25000D, 25000D), new CoordRect(625000D, 412500D, 25000D, 25000D), new CoordRect(-12500D, 462500D, 17500D, 50000D)
    };
    private double deltaValue1[][] = {
        {
            0.0D, 50000D
        }, {
            0.0D, 50000D
        }, {
            0.0D, 10000D
        }, {
            -70378D, -136D
        }, {
            -144738D, -2161D
        }, {
            23510D, -111D
        }
    };
    private double deltaValue2[][] = {
        {
            0.0D, -50000D
        }, {
            0.0D, -50000D
        }, {
            0.0D, -10000D
        }, {
            70378D, 136D
        }, {
            144738D, 2161D
        }, {
            -23510D, 111D
        }
    };
}