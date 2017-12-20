# nsBuild

Essa ferramenta tem como objetivo compilar, gerar e gerir depend�ncias de projetos em Delphi.

----------------------------------------------------------------
SINTAXE ESPERADA:

nsbuild <nome do projeto/update/clean/make:bpl> [debug (default)/release]
----------------------------------------------------------------


Conceitos importantes:

- nome do projeto - Especifica o projeto objetivo a ser compilado (todos os projetos - ainda n�o compilados, ou alterados - na �rvore de depend�ncias do mesmo ser�o compilados � priori).

- update - Compila todos os projetos dispon�veis (ainda n�o compilados, ou alterados), respeitando a ordem de depend�ncias entre os mesmos.

- clean - Apaga todas as DCUs e limpa a cache de controle dos projetos compilados (ATEN��O: Ap�s ser chamado o clean, uma chamada ao comando 'nsbuild update' ser� equivalente ao antigo build.bat na op��o zero).

- debug/release - Modo de build, isto �, gera os execut�veis em modo debug (para depura��o) ou modo de entrega (release).

- make:bpl - cria um projeto do tipo BPL se baseando no projeto .dpr.


Obs. 1: Utilize o seguinte comando para visualizar o manual de uso: 'nsbuild /?'

Obs. 2: Para for�ar a recompila��o de todos os projetos (antigo build.bat na op��o 0), � preciso usar sequencialmente os comandos:

nsbuild clean
nsbuild update
