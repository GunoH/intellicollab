package nl.guno.collab.intellij;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PluginUtilTest {

    @Test
    public void testExtractHostFromUrl() throws Exception {
        assertEquals("abc.com", PluginUtil.extractHostFromUrl("http://abc.com"));
        assertEquals("abc.com", PluginUtil.extractHostFromUrl("https://abc.com"));
        assertEquals("abc.com", PluginUtil.extractHostFromUrl("http://abc.com/"));
        assertEquals("abc.com", PluginUtil.extractHostFromUrl("http://abc.com/def"));
        assertEquals("abc.com", PluginUtil.extractHostFromUrl("http://abc.com/def/ghi"));
        assertEquals("abc.com", PluginUtil.extractHostFromUrl("http://abc.com:80"));
        assertEquals("abc.com", PluginUtil.extractHostFromUrl("http://abc.com?foo=123"));
        assertEquals("abc.com", PluginUtil.extractHostFromUrl("http://abc.com:80/?foo=123"));
        assertEquals("abc.com", PluginUtil.extractHostFromUrl("http://abc.com#foo"));
        assertEquals("abc.com", PluginUtil.extractHostFromUrl("http://abc.com:80/#foo"));
        assertEquals("abc.com", PluginUtil.extractHostFromUrl("http://abc.com:80/&foo=123"));
        assertEquals("abc.com", PluginUtil.extractHostFromUrl("https://abc.com:80/def/ghi?foo=123&bar=456"));

    }
}