# Feature Specification: Organizador e Player de Acervo Musical

**Feature Branch**: `001-create-feature-branch`

**Created**: 2026-06-08

**Status**: Draft

**Input**: User description: "Tenho 1TB de arquivos mp3..." e analise da implementacao legada em D:\polzinMusic\musicas.

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Importar, enriquecer e organizar albuns (Priority: P1)

Como colecionador musical, quero importar uma pasta com arquivos de audio, revisar os metadados sugeridos e finalizar a organizacao em uma estrutura padronizada para manter meu acervo consistente e pesquisavel.

**Why this priority**: Essa jornada entrega o valor principal do produto: tirar um acervo desorganizado e torná-lo navegavel, com baixa perda de metadados.

**Independent Test**: Pode ser testada importando uma pasta com arquivos de multiplos albuns, revisando as sugestoes e validando a copia final em pastas organizadas com nomes corretos.

**Acceptance Scenarios**:

1. **Given** uma pasta de importacao com MP3s, **When** o usuario inicia a importacao, **Then** o sistema varre subpastas e monta uma lista de faixas detectadas.
2. **Given** uma faixa com metadados incompletos, **When** o sistema processa a faixa, **Then** ele tenta ler metadados na ordem ID3v2, depois ID3v1 e, se necessario, infere por pasta/nome de arquivo.
3. **Given** metadados locais lidos, **When** o sistema consulta um webservice gratuito de catalogo musical, **Then** ele apresenta sugestoes para revisao humana por arquivo antes da gravacao final.
4. **Given** revisao concluida, **When** o usuario confirma a organizacao, **Then** os arquivos sao copiados para a pasta destino configurada seguindo o padrao de artista/tipo de album/ano-nome e numero de faixa com dois digitos.
5. **Given** exista capa local na pasta de origem ou imagem encontrada no Cover Art Archive, **When** o usuario escolhe a arte na revisao, **Then** o sistema embute a arte escolhida nas MP3 organizadas e, se aplicavel, guarda uma copia cache da imagem no album.
6. **Given** duas ou mais versoes da mesma musica forem detectadas na importacao, **When** o usuario abre a resolucao de duplicatas, **Then** o sistema exibe comparacao faixa a faixa com tamanho de arquivo e indicadores de qualidade para o usuario escolher qual versao manter na pasta organizada.

---

### User Story 2 - Navegar por relacionamento musical (Priority: P2)

Como usuario, quero que o acervo gere links por pais e por relacoes de cover para navegar por contexto musical sem duplicar arquivos.

**Why this priority**: A navegacao por contexto (pais e cover) aumenta muito a descoberta no acervo sem impactar o arquivo principal.

**Independent Test**: Pode ser testada com um album de artista conhecido e uma musica cover, verificando se os links de pais e cover sao criados e funcionais.

**Acceptance Scenarios**:

1. **Given** um artista com pais de origem identificado, **When** o album e organizado, **Then** o sistema cria estrutura por pais contendo link para a pasta do artista.
2. **Given** uma faixa identificada como cover, **When** a organizacao e confirmada, **Then** o sistema registra o artista original e cria links de navegacao de cover no filesystem.
3. **Given** sistemas operacionais diferentes, **When** os links sao gerados no Windows ou Linux, **Then** o sistema garante comportamento consistente e reprocessamento quando necessario.
4. **Given** estilos musicais identificados a partir das fontes externas, **When** a organizacao e confirmada, **Then** o sistema cria estrutura em Paises, Estilos e Marcacoes sem acentos no filesystem.

---

### User Story 3 - Reproduzir e registrar historico de escuta (Priority: P3)

Como usuario, quero usar o mesmo sistema como player para tocar meu acervo e registrar quando ouvi cada album/faixa para guiar futuras sugestoes.

**Why this priority**: Consolidar organizacao e reproducao no mesmo sistema reduz friccao e permite recomendacao baseada em historico real.

**Independent Test**: Pode ser testada reproduzindo albuns, pausando/retomando e verificando registro de ultima escuta no banco local.

**Acceptance Scenarios**:

1. **Given** um album organizado no catalogo, **When** o usuario inicia reproducao no player, **Then** o sistema reproduz as faixas em ordem correta.
2. **Given** reproducao em andamento, **When** o usuario aciona play/pause por global key do sistema operacional, **Then** o player responde imediatamente.
3. **Given** uma faixa ou album reproduzido, **When** a reproducao e concluida ou interrompida com progresso valido, **Then** a data/hora de ultima escuta e registrada no banco local.

---

### User Story 4 - Classificar e receber sugestoes personalizadas (Priority: P4)

Como usuario, quero marcar musicas com tags customizadas e avaliar albuns para receber sugestoes equilibradas entre qualidade e tempo sem escutar.

**Why this priority**: Personalizacao transforma o catalogo em curadoria pessoal e reduz repeticao indesejada.

**Independent Test**: Pode ser testada cadastrando tags e notas, depois solicitando sugestoes e verificando ordenacao por nota e tempo sem reproducao.

**Acceptance Scenarios**:

1. **Given** uma musica no catalogo, **When** o usuario adiciona marcacoes customizadas (ex.: lenta, cover legal, animada), **Then** as marcacoes ficam salvas e filtraveis.
2. **Given** um album avaliado pelo usuario, **When** o mecanismo de sugestao e executado, **Then** ele prioriza albuns com nota alta e maior tempo sem escuta.
3. **Given** um historico de reproducoes recente, **When** novas sugestoes sao solicitadas, **Then** o sistema evita repeticoes excessivas em janela curta.
4. **Given** artistas ja cadastrados no acervo, **When** a verificacao periodica online e executada, **Then** o sistema identifica albuns ausentes em MP3 e notifica o usuario com a lista de pendencias por artista.

### Edge Cases

- Pasta de importacao contendo arquivos corrompidos, duplicados, sem tag ou com extensoes mistas.
- Falha temporaria do webservice gratuito, limite de taxa ou divergencia de metadados entre fontes.
- Albuns com multiplas midias (CD1, CD2) e faixas sem numero consistente.
- Artistas com nomes equivalentes, pseudonimos ou homonimos que possam gerar associacoes incorretas.
- Diferencas de permissao para criacao de links no Windows e Linux.
- Caminhos longos, caracteres invalidos para filesystem e conflito de nomes de arquivo no destino.
- Duplicatas com mesmo nome de musica, mas duracao/bitrate diferentes ou tags conflitantes.
- Duplicatas de mesma faixa em albuns diferentes (studio, compilacao, ao vivo) que nao devem ser mescladas indevidamente.

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: O sistema MUST permitir ao usuario importar uma pasta raiz e varrer recursivamente subpastas com arquivos de audio.
- **FR-002**: O sistema MUST processar metadados de cada faixa com estrategia de fallback: ID3v2, depois ID3v1, depois inferencia por estrutura de pastas e nome de arquivo.
- **FR-003**: O sistema MUST consultar pelo menos um webservice gratuito de metadados musicais para enriquecer artista, album, faixa, ano, pais e relacionamentos.
- **FR-004**: O sistema MUST apresentar uma tela de revisao por arquivo antes da organizacao final, permitindo aceitar/editar os dados propostos.
- **FR-005**: O sistema MUST copiar os arquivos revisados para pasta destino configuravel, sem alterar os arquivos de origem.
- **FR-006**: O sistema MUST organizar albuns nas categorias Estudio, Compilacao, Single, EP e Ao Vivo.
- **FR-007**: O sistema MUST aplicar o padrao de pasta `<organizada>/Artistas/<nome_artista>/<tipo_album>/<ano - nome album>`.
- **FR-008**: O sistema MUST renomear faixas no destino com prefixo de numero de faixa em dois digitos no formato `NN - Titulo.extensao`.
- **FR-009**: O sistema MUST manter catalogo local persistente com artistas, albuns, faixas, pais, relacoes de cover, marcacoes customizadas, nota de album e historico de escuta.
- **FR-010**: O sistema MUST gerar navegacao por pais no filesystem em `<organizada>/Paises/<pais>/<link artista>`.
- **FR-011**: O sistema MUST identificar automaticamente relacoes de cover de usando relacionamento de gravacao/obra em bases abertas e associar artista original.
- **FR-012**: O sistema MUST criar links de navegacao para covers no filesystem, com suporte a navegacao em pelo menos uma direcao (cover -> original).
- **FR-013**: O sistema MUST funcionar em Windows e Linux, incluindo criacao e validacao de links em ambos.
- **FR-014**: O sistema MUST oferecer player integrado para reproducao do acervo organizado.
- **FR-015**: O sistema MUST registrar a ultima escuta por album e por faixa.
- **FR-016**: O sistema MUST permitir marcacoes customizadas por musica e nota por album.
- **FR-017**: O sistema MUST sugerir o que ouvir com base em nota do album, tempo sem escuta e mecanismo antirrepeticao.
- **FR-018**: O sistema MUST suportar global keys do sistema operacional, incluindo ao menos play/pause.
- **FR-019**: O sistema MUST preservar e evoluir comportamentos relevantes ja presentes no legado, incluindo importacao por pasta, enriquecimento via MusicBrainz, escrita de tags e persistencia de atalhos/relacionamentos.
- **FR-020**: O sistema MUST executar verificacoes periodicas online para cada artista do catalogo e avisar o usuario sobre albuns identificados em fontes abertas que ainda nao existem no acervo local em MP3.
- **FR-021**: O sistema MUST organizar o filesystem por estilo musical em `<organizada>/Estilos/<style_name>/<artist_link>`, usando estilos obtidos das fontes externas e nomes normalizados sem acentos.
- **FR-022**: O sistema MUST organizar o filesystem por marcações de musica em `<organizada>/Marcacoes/<nome_marcacao>/<music_link>`, com nomes normalizados sem acentos.
- **FR-023**: O sistema MUST permitir ao usuario escolher a capa do album entre imagem local da pasta de origem e imagem obtida do Cover Art Archive, e MUST embutir a arte selecionada nas MP3 organizadas, mantendo opcionalmente uma copia cache da imagem no album.
- **FR-024**: O sistema MUST detectar duplicatas durante a importacao e oferecer resolucao musica por musica, exibindo comparacao objetiva de versoes (tamanho do arquivo, bitrate, sample rate, duracao e origem) para o usuario escolher qual manter na biblioteca organizada.
- **FR-025**: O sistema MUST registrar no banco local a decisao de duplicata (versao mantida e descartadas) para auditoria e reprocessamento futuro.

### Non-Functional Requirements *(mandatory)*

- **NFR-001 (Code Quality)**: Toda alteracao deve seguir padrao de legibilidade, responsabilidades claras e cobertura de validacoes para fluxos criticos de importacao, organizacao e player.
- **NFR-002 (Testing)**: O produto deve possuir testes automatizados para parse de metadados, regras de organizacao, criacao de links, reproducao e algoritmo de sugestao.
- **NFR-003 (UX Consistency)**: Telas de importacao, revisao e player devem manter padrao consistente de navegacao, feedback de progresso, mensagens de erro e termos usados.
- **NFR-004 (Performance)**: O sistema deve permitir processamento em lote de acervos extensos com comportamento estavel, progresso visivel e retomada segura apos falhas.

### Key Entities *(include if feature involves data)*

- **ArquivoImportado**: Representa um arquivo detectado na importacao, com caminho original, extensao, status de leitura, fonte dos metadados e indicador de corrupcao.
- **Faixa**: Representa musica individual com titulo, numero, duracao, dados tecnicos basicos, tags customizadas, artista principal e artista original (quando cover).
- **Album**: Representa conjunto de faixas com nome, ano original/relancamento, tipo (Estudio, Compilacao, Single, EP, Ao Vivo), nota do usuario e timestamps de ultima escuta.
- **Artista**: Representa artista/grupo com nome, pais de origem, relacionamentos e colecao de albuns.
- **EstiloMusical**: Representa um estilo/tag musical consolidado a partir de fontes externas, com nome normalizado e origem da classificacao.
- **MarcacaoMusica**: Representa uma classificacao customizada aplicada a uma musica e usada para navegacao no filesystem.
- **CapaAlbum**: Representa a arte escolhida para o album, com origem (local ou Cover Art Archive), caminho/codigo da imagem e status de embutimento nas faixas.
- **GrupoDuplicata**: Representa um conjunto de candidatas consideradas a mesma faixa no contexto de importacao.
- **CandidataDuplicata**: Representa cada versao comparada de uma mesma musica, com metrica de qualidade tecnica e origem do arquivo.
- **ResolucaoDuplicata**: Representa a decisao do usuario sobre qual candidata foi mantida e quais foram descartadas.
- **RelacaoCover**: Liga faixa cover ao artista original e registra a confianca/origem da associacao.
- **AtalhoNavegacao**: Representa link de filesystem por pais, relacionamento ou cover, com status por sistema operacional.
- **EventoEscuta**: Registro temporal de reproducao para calculo de historico e recomendacao.
- **SugestaoReproducao**: Item recomendado com justificativa baseada em nota, recencia e diversificacao.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: Em uma amostra de 10.000 arquivos, pelo menos 95% das faixas sao importadas com metadados minimos suficientes para revisao (titulo, artista e album por tag ou inferencia).
- **SC-002**: Pelo menos 90% das faixas com correspondencia em base aberta recebem enriquecimento adicional apos consulta ao webservice.
- **SC-003**: Em testes de aceitacao, 100% dos albuns aprovados na revisao sao copiados para a estrutura de pastas padrao sem sobrescrever origem.
- **SC-004**: Pelo menos 99% dos arquivos exportados seguem o formato `NN - Titulo.extensao` com dois digitos no numero da faixa.
- **SC-005 (Quality)**: Todos os artefatos alterados passam validacoes de qualidade e testes obrigatorios definidos para a feature.
- **SC-006 (UX)**: Pelo menos 85% dos usuarios de teste concluem o fluxo completo (importar, revisar, organizar) sem suporte externo.
- **SC-007 (Performance)**: Em acervo de referencia de 100.000 faixas, a interface mantém feedback de progresso continuo e permite retomada apos interrupcao sem retrabalho integral.
- **SC-008**: Pelo menos 95% dos comandos de global key mapeados (incluindo play/pause) respondem corretamente durante reproducao ativa.
- **SC-009**: O mecanismo de sugestao reduz em pelo menos 40% a repeticao de albuns em janela de 30 dias comparado a reproducao cronologica simples.
- **SC-010**: Em cada ciclo de verificacao periodica, pelo menos 95% dos artistas cadastrados sao comparados com catalogo online e a notificacao de albuns ausentes e disponibilizada ao usuario em ate 10 minutos apos o fim da varredura.
- **SC-011**: Pelo menos 90% dos artistas com estilo identificado pelas fontes externas sao espelhados na estrutura `<organizada>/Estilos/<style_name>/<artist_link>` usando nomes normalizados sem acentos.
- **SC-012**: Pelo menos 90% das marcacoes customizadas ativas das musicas sao espelhadas em `<organizada>/Marcacoes/<nome_marcacao>/<music_link>` usando nomes normalizados sem acentos.
- **SC-013**: Pelo menos 95% dos albuns com imagem local ou imagem disponivel no Cover Art Archive permitem selecao de arte na revisao e embutem a capa escolhida nas MP3 organizadas.
- **SC-014**: Em amostra de importacao com duplicatas conhecidas, pelo menos 95% das duplicatas detectadas apresentam comparacao completa (tamanho, bitrate, sample rate e duracao) e permitem decisao explicita do usuario antes da organizacao final.

## Assumptions

- O usuario possui permissao de leitura na pasta de origem e permissao de escrita na pasta organizada.
- O sistema prioriza audio em MP3 no MVP, podendo listar outras extensoes sem garantia de enriquecimento completo no primeiro ciclo.
- Existe conectividade de rede durante enriquecimento em webservice, com fallback para operacao local quando indisponivel.
- A revisao humana e obrigatoria antes da gravacao final para reduzir erros de associacao automatica.
- Links de filesystem podem ter requisitos de permissao diferentes entre Windows e Linux; o sistema deve orientar o usuario quando houver restricao.
- O legado em D:\polzinMusic\musicas traz referencias funcionais relevantes: leitura/escrita de tags ID3, integracao com MusicBrainz, persistencia local e gerenciamento de atalhos por sistema operacional.
