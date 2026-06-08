# Research - Organizador e Player de Acervo Musical

## Decision 1: Plataforma desktop cross-platform em JVM
- Decision: Adotar aplicacao desktop JVM (Java 21) com UI nativa multiplataforma e build para Windows/Linux.
- Rationale: Reaproveita conhecimento e conceitos do legado Java, facilita acesso a filesystem local e integra melhor com bibliotecas maduras de audio e tags.
- Alternatives considered: Electron/Tauri (maior overhead e nova stack), Python desktop (menor alinhamento com legado e packaging mais sensivel).

## Decision 2: Persistencia local em SQLite
- Decision: Usar SQLite como banco local principal para catalogo, historico de escuta, tags customizadas e cache de sincronizacao.
- Rationale: Zero administracao, robusto para uso local, bom suporte em Windows/Linux e facil backup/portabilidade.
- Alternatives considered: H2/HSQLDB (viaveis, mas ecossistema e ferramentas de inspeção menos comuns para uso continuo do usuario), arquivos JSON (fraco para consulta e consistencia transacional).

## Decision 3: Pipeline de metadados com fallback deterministico
- Decision: Implementar leitura de metadados na ordem: ID3v2 -> ID3v1 -> inferencia por pasta/nome do arquivo.
- Rationale: Reflete requisito funcional e comportamento legado validado, maximizando taxa de importacao util.
- Alternatives considered: Falhar sem ID3v2 (perde muitos arquivos), inferencia primeiro (maior risco de erro sem aproveitar tags existentes).

## Decision 4: Enriquecimento online via MusicBrainz + cache local
- Decision: Usar MusicBrainz como fonte principal gratuita de metadados e relacionamentos (incluindo cover-de), com cache local e controle de taxa.
- Rationale: Fonte aberta com modelagem rica de relacoes artista/gravação/obra e historico de uso no legado.
- Alternatives considered: Last.fm/Discogs somente (cobertura/utilizacao e termos variam), multiplas fontes no MVP (maior complexidade inicial).

## Decision 5: Contrato de organizacao de filesystem e links simbolicos
- Decision: Padronizar layout de saida por artista/tipo de album/ano e criar links de navegacao por pais e cover, com camada de compatibilidade por SO.
- Rationale: Entrega navegacao rica sem duplicar arquivos e respeita requisito de suporte Windows/Linux.
- Alternatives considered: Duplicar pastas por pais/cover (desperdicio de espaco), somente navegacao no banco (nao atende requisito de links no filesystem).

## Decision 6: Player integrado com hotkeys globais
- Decision: Implementar player local integrado com captura de global key (minimo play/pause) e registro de ultima escuta por faixa/album.
- Rationale: Requisito central de uso diario e base para recomendacao antirrepeticao.
- Alternatives considered: Player externo sem integracao (nao registra contexto completo), hotkeys locais apenas em foco (nao atende requisito global).

## Decision 7: Recomendacao baseada em score hibrido
- Decision: Calcular recomendacao por score composto de nota do album, tempo sem escuta e penalidade de repeticao recente.
- Rationale: Cumpre objetivo de sugerir melhor conteudo e reduzir repeticao.
- Alternatives considered: Ordenacao por nota somente (repete muito), random puro (baixa relevancia percebida).

## Decision 8: Processamento de alto volume orientado a jobs
- Decision: Modelar importacao/sincronizacao como jobs retomaveis com checkpoint, progresso incremental e operacoes em lote.
- Rationale: Necessario para acervo de grande escala (ate 1TB) com resiliencia a interrupcoes.
- Alternatives considered: Fluxo unico sem checkpoint (alto retrabalho em falhas), transacao unica gigante (risco operacional e pior UX).

## Decision 9: Estrategia de testes obrigatorios por camada
- Decision: Exigir testes unitarios (parse e regras), integracao (banco, filesystem, sincronizacao) e regressao (cover, recomendacao, links cross-OS).
- Rationale: Alinha com constituicao e reduz risco em funcionalidades de dados e IO.
- Alternatives considered: Testes manuais predominantes (insuficiente para regressao), apenas unitarios (nao cobrem IO real).
