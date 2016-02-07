package com.georgequotes.objects;

import java.util.Date;

public class Point
{
	public Point() {}
	public Point(Point p)
	{
		o = p.o;
		h = p.h;
		l = p.l;
		c = p.c;
		v = p.v;
		dt = p.dt;
	}
	
	public double o, h, l, c, v;
	public Date dt;
}
