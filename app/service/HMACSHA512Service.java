package service;

import com.google.inject.ImplementedBy;
import serviceImpl.HMACSHA512ServiceImpl;

@ImplementedBy(HMACSHA512ServiceImpl.class)
public interface HMACSHA512Service
{
	public String getHmacSHA512(
	    String data,
	    String key);
}
