//package airy;

public class airy {//test airy include all
	public static double ai;
	public static double bi;
	public static double aip;
	public static double bip;

	static double ri,rk,rip,rkp;
	static double rj,ry,rjp,ryp;
	
	static double gam1,gam2,gampl,gammi;
	
	static double[] c1_d = {
			-1.142022680371168e0,6.5165112670737e-3,
			3.087090173086e-4,-3.4706269649e-6,6.9437664e-9,
			3.67795e-11,-1.356e-13};
	static double[] c2_d = {
			1.843740587300905e0,-7.68528408447867e-2,
			1.2719271366546e-3,-4.9717367042e-6,-3.31261198e-8,
			2.423096e-10,-1.702e-13,-1.49e-15};
		
	public static void bessik(double x, double xnu)//(const DP x, const DP xnu, DP &ri, DP &rk, DP &rip, DP &rkp)
	{
		final int MAXIT=10000;
		final double EPS=1e-14;
		final double FPMIN=Double.MIN_VALUE/EPS;
		final double XMIN=2.0, PI=3.141592653589793;
		double a,a1,b,c,d,del,del1,delh,dels,e,f,fact,fact2,ff,h,p,pimu,q,q1,q2,qnew,ril,ril1,rimu,rip1,ripl,
			ritemp,rk1,rkmu,rkmup,rktemp,s,sum,sum1,x2,xi,xi2,xmu,xmu2;
		int i,l,nl;

		if (x <= 0.0 || xnu < 0.0) System.out.println("bad arguments in bessik");
		nl=(int)(xnu+0.5);
		xmu=xnu-nl;
		xmu2=xmu*xmu;
		xi=1.0/x;
		xi2=2.0*xi;
		h=xnu*xi;
		if (h < FPMIN) h=FPMIN;
		b=xi2*xnu;
		d=0.0;
		c=h;
		for (i=0;i<MAXIT;i++) {
			b += xi2;
			d=1.0/(b+d);
			c=b+1.0/c;
			del=c*d;
			h=del*h;
			if (Math.abs(del-1.0) <= EPS) break;
		}
		if (i >= MAXIT)
			System.out.println("x too large in bessik; try asymptotic expansion");
		ril=FPMIN;
		ripl=h*ril;
		ril1=ril;
		rip1=ripl;
		fact=xnu*xi;
		for (l=nl-1;l >= 0;l--) {
			ritemp=fact*ril+ripl;
			fact -= xi;
			ripl=fact*ritemp+ril;
			ril=ritemp;
		}
		f=ripl/ril;
		if (x < XMIN) {
			x2=0.5*x;
			pimu=PI*xmu;
			fact = (Math.abs(pimu) < EPS ? 1.0 : pimu/Math.sin(pimu));
			d = -Math.log(x2);
			e=xmu*d;
			fact2 = (Math.abs(e) < EPS ? 1.0 : Math.sinh(e)/e);
			beschb(xmu);
			ff=fact*(gam1*Math.cosh(e)+gam2*fact2*d);
			sum=ff;
			e=Math.exp(e);
			p=0.5*e/gampl;
			q=0.5/(e*gammi);
			c=1.0;
			d=x2*x2;
			sum1=p;
			for (i=1;i<=MAXIT;i++) {
				ff=(i*ff+p+q)/(i*i-xmu2);
				c *= (d/i);
				p /= (i-xmu);
				q /= (i+xmu);
				del=c*ff;
				sum += del;
				del1=c*(p-i*ff);
				sum1 += del1;
				if (Math.abs(del) < Math.abs(sum)*EPS) break;
			}
			if (i > MAXIT) System.out.println("bessk series failed to converge");
			rkmu=sum;
			rk1=sum1*xi2;
		} else {
			b=2.0*(1.0+x);
			d=1.0/b;
			h=delh=d;
			q1=0.0;
			q2=1.0;
			a1=0.25-xmu2;
			q=c=a1;
			a = -a1;
			s=1.0+q*delh;
			for (i=1;i<MAXIT;i++) {
				a -= 2*i;
				c = -a*c/(i+1.0);
				qnew=(q1-b*q2)/a;
				q1=q2;
				q2=qnew;
				q += c*qnew;
				b += 2.0;
				d=1.0/(b+a*d);
				delh=(b*d-1.0)*delh;
				h += delh;
				dels=q*delh;
				s += dels;
				if (Math.abs(dels/s) <= EPS) break;
			}
			if (i >= MAXIT) System.out.println("bessik: failure to converge in cf2");
			h=a1*h;
			rkmu=Math.sqrt(PI/(2.0*x))*Math.exp(-x)/s;
			rk1=rkmu*(xmu+x+0.5-h)*xi;
		}
		rkmup=xmu*xi*rkmu-rk1;
		rimu=xi/(f*rkmu-rkmup);
		ri=(rimu*ril1)/ril;
		rip=(rimu*rip1)/ril;
		for (i=1;i <= nl;i++) {
			rktemp=(xmu+i)*xi2*rk1+rkmu;
			rkmu=rk1;
			rk1=rktemp;
		}
		rk=rkmu;
		rkp=xnu*xi*rkmu-rk1;
	}
	
	private static void beschb(double x) {
		final int NUSE1=7, NUSE2=8;

		double xx;

		xx=8.0*x*x-1.0;
		gam1=chebev_c1d(-1.0,1.0,NUSE1,xx);
		gam2=chebev_c2d(-1.0,1.0,NUSE2,xx);
		gampl= gam2-x*gam1;
		gammi= gam2+x*gam1;
		
	}

	private static double chebev_c1d(double a, double b,int m, double x) {
		double d=0.0,dd=0.0,sv,y,y2;
		int j;

		if ((x-a)*(x-b) > 0.0)
			System.out.println("x not in range in routine chebev");
		y2=2.0*(y=(2.0*x-a-b)/(b-a));
		for (j=m-1;j>0;j--) {
			sv=d;
			d=y2*d-dd+c1_d[j];
			dd=sv;
		}
		return y*d-dd+0.5*c1_d[0];
	}
	private static double chebev_c2d(double a, double b,int m, double x) {
		double d=0.0,dd=0.0,sv,y,y2;
		int j;

		if ((x-a)*(x-b) > 0.0)
			System.out.println("x not in range in routine chebev");
		y2=2.0*(y=(2.0*x-a-b)/(b-a));
		for (j=m-1;j>0;j--) {
			sv=d;
			d=y2*d-dd+c2_d[j];
			dd=sv;
		}
		return y*d-dd+0.5*c2_d[0];
	}
	public static double MAX(double a, double b){
		return b > a ? (b) : (a);
	}
	
	public static double SIGN(double a, double b){
		return b >= 0 ? (a >= 0 ? a : -a) : (a >= 0 ? -a : a);
	}
	
	public static void main_airy(double x)
	{
		final double PI=3.141592653589793238, ONOVRT=0.577350269189626;
		final double THIRD=(1.0/3.0), TWOTHR=2.0*THIRD;
		double absx,rootx,z;

		absx=Math.abs(x);
		rootx=Math.sqrt(absx);
		z=TWOTHR*absx*rootx;
		if (x > 0.0) {
			bessik(z,THIRD);
			ai=rootx*ONOVRT*rk/PI;
			bi=rootx*(rk/PI+2.0*ONOVRT*ri);
			bessik(z,TWOTHR);
			aip = -x*ONOVRT*rk/PI;
			bip=x*(rk/PI+2.0*ONOVRT*ri);
		} else if (x < 0.0) {
			bessjy(z,THIRD);
			ai=0.5*rootx*(rj-ONOVRT*ry);
			bi = -0.5*rootx*(ry+ONOVRT*rj);
			bessjy(z,TWOTHR);
			aip=0.5*absx*(ONOVRT*ry+rj);
			bip=0.5*absx*(ONOVRT*rj-ry);
		} else {
			ai=0.355028053887817;
			bi=ai/ONOVRT;
			aip = -0.258819403792807;
			bip = -aip/ONOVRT;
		}
	}

	private static void bessjy(double x, double xnu) {//(const DP x, const DP xnu, DP &rj, DP &ry, DP &rjp, DP &ryp)
			final int MAXIT=10000;
			final double EPS=1e-14;
			final double FPMIN=Double.MIN_VALUE/EPS;
			final double XMIN=2.0, PI=3.141592653589793;
			double a,b,br,bi,c,cr,ci,d,del,del1,den,di,dlr,dli,dr,e,f,fact,fact2,
				fact3,ff,gam,h,p,pimu,pimu2,q,r,rjl,
				rjl1,rjmu,rjp1,rjpl,rjtemp,ry1,rymu,rymup,rytemp,sum,sum1,
				temp,w,x2,xi,xi2,xmu,xmu2;
			int i,isign,l,nl;

			if (x <= 0.0 || xnu < 0.0)
				System.out.println("bad arguments in bessjy");
			nl=(int) (x < XMIN ? (int)(xnu+0.5) : MAX(0,(int)(xnu-x+1.5)));
			xmu=xnu-nl;
			xmu2=xmu*xmu;
			xi=1.0/x;
			xi2=2.0*xi;
			w=xi2/PI;
			isign=1;
			h=xnu*xi;
			if (h < FPMIN) h=FPMIN;
			b=xi2*xnu;
			d=0.0;
			c=h;
			for (i=0;i<MAXIT;i++) {
				b += xi2;
				d=b-d;
				if (Math.abs(d) < FPMIN) d=FPMIN;
				c=b-1.0/c;
				if (Math.abs(c) < FPMIN) c=FPMIN;
				d=1.0/d;
				del=c*d;
				h=del*h;
				if (d < 0.0) isign = -isign;
				if (Math.abs(del-1.0) <= EPS) break;
			}
			if (i >= MAXIT)
				System.out.println("x too large in bessjy; try asymptotic expansion");
			rjl=isign*FPMIN;
			rjpl=h*rjl;
			rjl1=rjl;
			rjp1=rjpl;
			fact=xnu*xi;
			for (l=nl-1;l>=0;l--) {
				rjtemp=fact*rjl+rjpl;
				fact -= xi;
				rjpl=fact*rjtemp-rjl;
				rjl=rjtemp;
			}
			if (rjl == 0.0) rjl=EPS;
			f=rjpl/rjl;
			if (x < XMIN) {
				x2=0.5*x;
				pimu=PI*xmu;
				fact = (Math.abs(pimu) < EPS ? 1.0 : pimu/Math.sin(pimu));
				d = -Math.log(x2);
				e=xmu*d;
				fact2 = (Math.abs(e) < EPS ? 1.0 : Math.sinh(e)/e);
				beschb(xmu);
				ff=2.0/PI*fact*(gam1*Math.cosh(e)+gam2*fact2*d);
				e=Math.exp(e);
				p=e/(gampl*PI);
				q=1.0/(e*PI*gammi);
				pimu2=0.5*pimu;
				fact3 = (Math.abs(pimu2) < EPS ? 1.0 : Math.sin(pimu2)/pimu2);
				r=PI*pimu2*fact3*fact3;
				c=1.0;
				d = -x2*x2;
				sum=ff+r*q;
				sum1=p;
				for (i=1;i<=MAXIT;i++) {
					ff=(i*ff+p+q)/(i*i-xmu2);
					c *= (d/i);
					p /= (i-xmu);
					q /= (i+xmu);
					del=c*(ff+r*q);
					sum += del;
					del1=c*p-i*del;
					sum1 += del1;
					if (Math.abs(del) < (1.0+Math.abs(sum))*EPS) break;
				}
				if (i > MAXIT)
					System.out.println("bessy series failed to converge");
				rymu = -sum;
				ry1 = -sum1*xi2;
				rymup=xmu*xi*rymu-ry1;
				rjmu=w/(rymup-f*rymu);
			} else {
				a=0.25-xmu2;
				p = -0.5*xi;
				q=1.0;
				br=2.0*x;
				bi=2.0;
				fact=a*xi/(p*p+q*q);
				cr=br+q*fact;
				ci=bi+p*fact;
				den=br*br+bi*bi;
				dr=br/den;
				di = -bi/den;
				dlr=cr*dr-ci*di;
				dli=cr*di+ci*dr;
				temp=p*dlr-q*dli;
				q=p*dli+q*dlr;
				p=temp;
				for (i=1;i<MAXIT;i++) {
					a += 2*i;
					bi += 2.0;
					dr=a*dr+br;
					di=a*di+bi;
					if (Math.abs(dr)+Math.abs(di) < FPMIN) dr=FPMIN;
					fact=a/(cr*cr+ci*ci);
					cr=br+cr*fact;
					ci=bi-ci*fact;
					if (Math.abs(cr)+Math.abs(ci) < FPMIN) cr=FPMIN;
					den=dr*dr+di*di;
					dr /= den;
					di /= -den;
					dlr=cr*dr-ci*di;
					dli=cr*di+ci*dr;
					temp=p*dlr-q*dli;
					q=p*dli+q*dlr;
					p=temp;
					if (Math.abs(dlr-1.0)+Math.abs(dli) <= EPS) break;
				}
				if (i >= MAXIT) System.out.println("cf2 failed in bessjy");
				gam=(p-f)/q;
				rjmu=Math.sqrt(w/((p-f)*gam+q));
				rjmu=SIGN(rjmu,rjl);
				rymu=rjmu*gam;
				rymup=rymu*(p+q/gam);
				ry1=xmu*xi*rymu-rymup;
			}
			fact=rjmu/rjl;
			rj=rjl1*fact;
			rjp=rjp1*fact;
			for (i=1;i<=nl;i++) {
				rytemp=(xmu+i)*xi2*ry1-rymu;
				rymu=ry1;
				ry1=rytemp;
			}
			ry=rymu;
			ryp=xnu*xi*rymu-ry1;
		}
	
	public static void sphbes(int n, double x){
		final double RTPIO2=1.253314137315500251;
		double factor,order;
		double sj,sy,sjp,syp;
		
		if (n < 0 || x <= 0.0) System.out.println("bad arguments in sphbes");
		order=n+0.5;
		bessjy(x,order);
		factor=RTPIO2/Math.sqrt(x);
		sj=factor*rj;
		sy=factor*ry;
		sjp=factor*rjp-sj/(2.0*x);
		syp=factor*ryp-sy/(2.0*x);
	}
		
}
