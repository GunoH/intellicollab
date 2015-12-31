package nl.guno.ccollab.intellij;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PluginUtilTest {

    @Test
    public void testExtractHostFromUrl() throws Exception {
        assertEquals("abc.com", PluginUtil.extractHostFromUrl("http://abc.com"));
        assertEquals("abc.com", PluginUtil.extractHostFromUrl("http://abc.com/"));
        assertEquals("abc.com", PluginUtil.extractHostFromUrl("http://abc.com/def"));
        assertEquals("abc.com", PluginUtil.extractHostFromUrl("http://abc.com/def/ghi"));
        assertEquals("abc.com", PluginUtil.extractHostFromUrl("http://abc.com:80"));
        assertEquals("abc.com", PluginUtil.extractHostFromUrl("http://abc.com:80/"));
        assertEquals("abc.com", PluginUtil.extractHostFromUrl("http://abc.com:80/def"));
        assertEquals("abc.com", PluginUtil.extractHostFromUrl("http://abc.com:80/def/ghi"));

        assertEquals("abc.com", PluginUtil.extractHostFromUrl("https://abc.com"));
        assertEquals("abc.com", PluginUtil.extractHostFromUrl("https://abc.com/"));
        assertEquals("abc.com", PluginUtil.extractHostFromUrl("https://abc.com/def"));
        assertEquals("abc.com", PluginUtil.extractHostFromUrl("https://abc.com/def/ghi"));
        assertEquals("abc.com", PluginUtil.extractHostFromUrl("https://abc.com:80"));
        assertEquals("abc.com", PluginUtil.extractHostFromUrl("https://abc.com:80/"));
        assertEquals("abc.com", PluginUtil.extractHostFromUrl("https://abc.com:80/def"));
        assertEquals("abc.com", PluginUtil.extractHostFromUrl("https://abc.com:80/def/ghi"));
    }
}