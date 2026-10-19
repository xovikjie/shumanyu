package com.shumanyu.app;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Arrays;
public class WhiteEdgeBoundsTest {
 private int[] bounds(int[] pixels,int w,int h){return WhiteEdgeBounds.find(w,h,new WhiteEdgeBounds.Lines(){
  public void row(int y,int[] out){System.arraycopy(pixels,y*w,out,0,w);}
  public void column(int x,int[] out){for(int y=0;y<h;y++)out[y]=pixels[y*w+x];}
 });}
 @Test public void trimsOnlyOuterWhiteMargin(){int[] p=new int[400];Arrays.fill(p,0xffffffff);for(int y=2;y<18;y++)for(int x=2;x<18;x++)p[y*20+x]=0xff101010;assertArrayEquals(new int[]{2,2,18,18},bounds(p,20,20));}
 @Test public void preservesSingleDarkPixelAtEdge(){int[] p=new int[400];Arrays.fill(p,0xff000000);Arrays.fill(p,0,20,0xffffffff);p[7]=0xff808080;assertArrayEquals(new int[]{0,0,20,20},bounds(p,20,20));}
 @Test public void preservesInteriorWhiteGutter(){int[] p=new int[400];Arrays.fill(p,0xff000000);Arrays.fill(p,160,240,0xffffffff);assertArrayEquals(new int[]{0,0,20,20},bounds(p,20,20));}
 @Test public void allWhiteAndVeryWideMarginsRemainUnchanged(){int[] p=new int[400];Arrays.fill(p,0xffffffff);assertArrayEquals(new int[]{0,0,20,20},bounds(p,20,20));}
}
