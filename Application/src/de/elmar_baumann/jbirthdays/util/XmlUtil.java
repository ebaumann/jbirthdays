package de.elmar_baumann.jbirthdays.util;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

/**
 * @author Elmar Baumann
 */
public final class XmlUtil {

    public static final String ENCODING = "UTF-8";

    public static String marshal(Object object) throws JAXBException {
        if (object == null) {
            throw new NullPointerException("object == null");
        }
        StringWriter sw = new StringWriter();
        try {
            JAXBContext context = JAXBContext.newInstance(object.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, ENCODING);
            marshaller.marshal(object, sw);
        } finally {
            IoUtil.close(sw);
        }
        return sw.toString();
    }

    public static void marshal(Object object, OutputStream os) throws JAXBException {
        if (object == null) {
            throw new NullPointerException("object == null");
        }
        if (os == null) {
            throw new NullPointerException("os == null");
        }
        JAXBContext context = JAXBContext.newInstance(object.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.setProperty(Marshaller.JAXB_ENCODING, ENCODING);
        marshaller.marshal(object, os);
    }

    public static <T> T unmarshal(String xmlString, Class<T> type) throws JAXBException {
        if (type == null) {
            throw new NullPointerException("type == null");
        }
        if (!StringUtil.hasContent(xmlString)) {
            return null;
        }
        JAXBContext context = JAXBContext.newInstance(type);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        JAXBElement<T> jaxbElement = unmarshaller.unmarshal(new StreamSource(new StringReader(xmlString)), type);
        T result = jaxbElement.getValue();
        return result;
    }

    public static <T> T unmarshal(InputStream is, Class<T> type) throws JAXBException, UnsupportedEncodingException {
        JAXBContext context = JAXBContext.newInstance(type);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        JAXBElement<T> jaxbElement = unmarshaller.unmarshal(new StreamSource(new InputStreamReader(is, ENCODING)), type);
        T result = jaxbElement.getValue();
        return result;
    }

    private XmlUtil() {
    }
}
