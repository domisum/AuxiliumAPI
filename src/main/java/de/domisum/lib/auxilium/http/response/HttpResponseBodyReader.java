package de.domisum.lib.auxilium.http.response;

import java.io.IOException;
import java.io.InputStream;

public interface HttpResponseBodyReader<T>
{

	T read(InputStream inputStream) throws IOException;

}