package com.shumanyu.app;

/** Conservative edge-only trimming: never remove interior rows or non-white pixels. */
final class WhiteEdgeBounds {
    interface Lines { void row(int y, int[] out); void column(int x, int[] out); }
    static int[] find(int w, int h, Lines lines) {
        if (w < 4 || h < 4) return new int[]{0,0,w,h};
        int[] row=new int[w],col=new int[h];
        int top=0,bottom=h,left=0,right=w;
        int yCap=Math.max(1,h/5),xCap=Math.max(1,w/5);
        while(top<yCap){lines.row(top,row);if(!white(row,0,w))break;top++;}
        if(top==yCap)top=0;
        while(h-bottom<yCap){lines.row(bottom-1,row);if(!white(row,0,w))break;bottom--;}
        if(h-bottom==yCap)bottom=h;
        while(left<xCap){lines.column(left,col);if(!white(col,top,bottom))break;left++;}
        if(left==xCap)left=0;
        while(w-right<xCap){lines.column(right-1,col);if(!white(col,top,bottom))break;right--;}
        if(w-right==xCap)right=w;
        return new int[]{left,top,right,bottom};
    }
    private static boolean white(int[] line,int from,int to){
        for(int i=from;i<to;i++){
            int c=line[i];if((c>>>24)==0)continue;
            if(((c>>>16)&255)<254 || ((c>>>8)&255)<254 || (c&255)<254)return false;
        }
        return true;
    }
}
