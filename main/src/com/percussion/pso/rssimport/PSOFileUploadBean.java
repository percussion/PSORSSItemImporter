package com.percussion.pso.rssimport;



import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.multipart.MultipartFile;

public class PSOFileUploadBean {

	private MultipartFile m_file;
	private String m_rssImportParam;
	private Log m_log = LogFactory.getLog(this.getClass());
	
    public void setFile(MultipartFile file) {
       m_file = file;
       m_log.debug("File Set");
    }

    public MultipartFile getFile() {
        return m_file;
    }

	
	public void setRSSImportParam(String value) {
		m_rssImportParam = value;
	}
	
	public String getRSSImportParam() {
		return m_rssImportParam;
	}
	
	public InputStream getInputStream() throws IOException {
		InputStream is=null;
		if (m_file!=null) {
			m_log.debug("Getting input stream from file");
			is = m_file.getInputStream();
		} else if (m_rssImportParam!=null && m_rssImportParam.length()>0) {
			is = new ByteArrayInputStream(m_rssImportParam.getBytes());
			m_log.debug("Getting input stream from parameter");
		}
		return is;
	}
}
