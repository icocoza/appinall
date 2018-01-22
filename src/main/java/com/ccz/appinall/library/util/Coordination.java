package com.ccz.appinall.library.util;

public class Coordination {
    public void convertUTM2WGS(double d, double e)
    {
        setParameter(115.8D, -474.99000000000001D, -674.11000000000001D, 1.1599999999999999D, -2.3100000000000001D, -1.6299999999999999D, -6.4299999999999997D, 1.0D);
        TM2GP(6378137D, 0.0033528106647474805D, 0.0D, 500000D, 0.99960000000000004D, e, d);
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
}
