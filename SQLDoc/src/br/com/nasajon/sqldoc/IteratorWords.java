package br.com.nasajon.sqldoc;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IteratorWords implements Closeable {
	
	private File arquivo;
	private InputStreamReader isr;
	private FileInputStream fis;
	private BufferedReader br;
	private Iterator<String> it;
	
	public IteratorWords(File arquivo) throws FileNotFoundException, UnsupportedEncodingException {
		super();
		
		this.arquivo = arquivo;
		this.fis = new FileInputStream(this.arquivo);
		this.isr = new InputStreamReader(this.fis, "Cp1252");
		this.br = new BufferedReader(this.isr);
	}
	
	@Override
	public void close() throws IOException {
		if (this.br != null) {
			this.br.close();
			this.br = null;
		}
		if (this.isr != null) {
			this.isr.close();
			this.isr = null;
		}
		if (this.fis != null) {
			this.fis.close();
			this.fis = null;
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		
		this.close();
	}

	public String nextWord() throws IOException {
		if ((it == null || !it.hasNext()) && !novoIterator()) {
			return null;
		} else {
			return it.next();
		}
	}
	
	private boolean novoIterator() throws IOException {
		it = null;
		String linha = null;
		List<String> listaWords = new ArrayList<String>();
		
		do {
			listaWords.clear();
			linha = br.readLine();
			
			if (linha == null) {
				return false;
			}
			
			String[] vetorWords = linha.split(" ");
			for (String s : vetorWords) {
				s = s.trim();
				
				if (!s.equals("")) {
					listaWords.add(s);
				}
			}
			
			if (listaWords.size() > 0) {
				it = listaWords.iterator();
			}
		} while(it == null);
		
		return true;
	}
}
