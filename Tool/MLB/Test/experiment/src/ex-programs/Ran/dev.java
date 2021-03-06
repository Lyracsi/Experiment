//package Ran;

public class dev {
	public static int idum ;
	
	public static double gamdev(int ia, int idumx){
		idum = idumx;
		int j;
		double am,e,s,v1,v2,x,y;

		if (ia < 1) System.out.println("Error in routine gamdev");
		if (ia < 6) {
			x=1.0;
			for (j=1;j<=ia;j++) x *= ran1();
			x = -Math.log(x);
		} else {
			do {
				do {
					do {
						v1=ran1();
						v2=2.0*ran1()-1.0;
					} while (v1*v1+v2*v2 > 1.0);
					y=v2/v1;
					am=ia-1;
					s=Math.sqrt(2.0*am+1.0);
					x=s*y+am;
				} while (x <= 0.0);
				e=(1.0+y*y)*Math.exp(am*Math.log(x/am)-s*y);
			} while (ran1() > e);
		}
		return x;
	}
	
	public static double gasdev(int idumx){
		idum = idumx;
		int iset=0;
		double gset = 0;
		double fac,rsq,v1,v2;

		if (idum < 0) iset=0;
		if (iset == 0) {
			do {
				v1=2.0*ran1()-1.0;
				v2=2.0*ran1()-1.0;
				rsq=v1*v1+v2*v2;
			} while (rsq >= 1.0 || rsq == 0.0);
			fac=Math.sqrt(-2.0*Math.log(rsq)/rsq);
			gset=v1*fac;
			iset=1;
			return v2*fac;
		} else {
			iset=0;
			return gset;
		}
	}
	public static double expdev(int idumx){
		idum = idumx;
		double dum;

		do
			dum=ran1();
		while (dum == 0.0);
		return -Math.log(dum);
	}
	public static double poidev(double xm,int idumx){
		idum = idumx;
		final double PI=3.141592653589793238;
		double sq = 0,alxm = 0,g = 0,oldm=(-1.0);
		double em,t,y;

		if (xm < 12.0) {
			if (xm != oldm) {
				oldm=xm;
				g=Math.exp(-xm);
			}
			em = -1;
			t=1.0;
			do {
				++em;
				t *= ran1();
			} while (t > g);
		} else {
			if (xm != oldm) {
				oldm=xm;
				sq=Math.sqrt(2.0*xm);
				alxm=Math.log(xm);
				g=xm*alxm-gammln(xm+1.0);
			}
			do {
				do {
					y=Math.tan(PI*ran1());
					em=sq*y+xm;
				} while (em < 0.0);
					em=Math.floor(em);
					t=0.9*(1.0+y*y)*Math.exp(em*alxm-gammln(em+1.0)-g);
			} while (ran1() > t && idum<530511967);
		}
		return em;
	}
	public static double bnldev(double pp,  int n, int idumx){
		idum = idumx;
		final double PI=3.141592653589793238;
		int j;
		int nold=(-1);
		double am,em,g,angle,p,bnl,sq,t,y;
		double pold=(-1.0),pc = 0,plog = 0,pclog = 0,en = 0,oldg = 0;

		p=(pp <= 0.5 ? pp : 1.0-pp);
		am=n*p;
		if (n < 25) {
			bnl=0.0;
			for (j=0;j<n;j++)
				if (ran1() < p) ++bnl;
		} else if (am < 1.0) {
			g=Math.exp(-am);
			t=1.0;
			for (j=0;j<=n;j++) {
				t *= ran1();
				if (t < g) break;
			}
			bnl=(j <= n ? j : n);
		} else {
			if (n != nold) {
				en=n;
				oldg=gammln(en+1.0);
				nold=n;
			} if (p != pold) {
				pc=1.0-p;
				plog=Math.log(p);
				pclog=Math.log(pc);
				pold=p;
			}
			sq=Math.sqrt(2.0*am*pc);
			do {
				do {
					angle=PI*ran1();
					y=Math.tan(angle);
					em=sq*y+am;
				} while (em < 0.0 || em >= (en+1.0));
				em=Math.floor(em);
				t=1.2*sq*(1.0+y*y)*Math.exp(oldg-gammln(em+1.0)
					-gammln(en-em+1.0)+em*plog+(en-em)*pclog);
			} while (ran1() > t&& idum<530511967);
			bnl=em;
		}
		if (p != pp) bnl=n-bnl;
		return bnl;
	}
	 static double gammln(double xx){
		int j;
		double x,y,tmp,ser;
		final double[] cof={76.18009172947146,-86.50532032941677,
			24.01409824083091,-1.231739572450155,0.1208650973866179e-2,
			-0.5395239384953e-5};

		y=x=xx;
		tmp=x+5.5;
		tmp -= (x+0.5)*Math.log(tmp);
		ser=1.000000000190015;
		for (j=0;j<6;j++) ser += cof[j]/++y;
		return -tmp+Math.log(2.5066282746310005*ser/x);
	}
	 
	 static double ran1(){
		final int IA=16807,IM=2147483647,IQ=127773,IR=2836,NTAB=32;
		int NDIV=(1+(IM-1)/NTAB);
		double EPS=3.0e-16,AM=1.0/IM,RNMX=(1.0-EPS);
		int iy=0;
		int[] iv = new int[NTAB];
		int j,k;
		double temp;

		if (idum <= 0 || iy == 0) {
			if (-idum < 1) idum=1;
			else idum = -idum;
			for (j=NTAB+7;j>=0;j--) {
				k=idum/IQ;
				idum=IA*(idum-k*IQ)-IR*k;
				if (idum < 0) idum += IM;
				if (j < NTAB) iv[j] = idum;
			}
			iy=iv[0];
		}
		k=idum/IQ;
		idum=IA*(idum-k*IQ)-IR*k;
		if (idum < 0) idum += IM;
		j=iy/NDIV;
		iy=iv[j];
		iv[j] = idum;
		if ((temp=AM*iy) > RNMX) return RNMX;
		else return temp;
	}
}
