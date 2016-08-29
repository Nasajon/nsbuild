package br.com.nasajon.buildxmls;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainTeste {

	public static void main(String[] args) throws IOException {
		List<Unit> dependenciasUnit = InterpretadorUnit.extrairDependencias(new File("C:\\@work\\erp\\source\\desktop_new\\commonfeature\\limitedecredito\\configuracao\\commonfeature.limitedecredito.acesso.dto.pas"));
		
		for (Unit dependencia: dependenciasUnit) {
			System.out.println(dependencia.getNome());
		}
//		Set<Unit> su = InterpretadorDproj.extrairIncludes(new File("C:\\@work\\erp\\source\\desktop_new\\frameworks\\nsORM\\_package\\nsORM.dproj"));
//		
//		for (Unit dependencia: su) {
//			System.out.println(dependencia.getNome());
//		}
	}

}
