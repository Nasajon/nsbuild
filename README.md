# nsBuild

Essa ferramenta tem como objetivo compilar, gerar e gerir dependências de projetos em Delphi.

----------------------------------------------------------------
SINTAXE ESPERADA:

nsbuild <nome do projeto/update/clean/make:bpl> [debug (default)/release]
----------------------------------------------------------------


Conceitos importantes:

- nome do projeto - Especifica o projeto objetivo a ser compilado (todos os projetos - ainda não compilados, ou alterados - na árvore de dependências do mesmo serão compilados à priori).

- update - Compila todos os projetos disponíveis (ainda não compilados, ou alterados), respeitando a ordem de dependências entre os mesmos.

- clean - Apaga todas as DCUs e limpa a cache de controle dos projetos compilados (ATENÇÃO: Após ser chamado o clean, uma chamada ao comando 'nsbuild update' será equivalente ao antigo build.bat na opção zero).

- debug/release - Modo de build, isto é, gera os executáveis em modo debug (para depuração) ou modo de entrega (release).

- make:bpl - cria um projeto do tipo BPL se baseando no projeto .dpr.


Obs. 1: Utilize o seguinte comando para visualizar o manual de uso: 'nsbuild /?'

Obs. 2: Para forçar a recompilação de todos os projetos (antigo build.bat na opção 0), é preciso usar sequencialmente os comandos:
nsbuild clean
nsbuild update
