package be.gcroes.thesis.docproc.task;

import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

public class ClassPathURIResolver implements URIResolver {

    @Override
    public Source resolve(String href, String base) throws TransformerException {
        return new StreamSource(getClass().getResourceAsStream("/" + href));
    }

}
