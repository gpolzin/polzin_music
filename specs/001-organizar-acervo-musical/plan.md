# Implementation Plan: Organizador e Player de Acervo Musical

**Branch**: `001-create-feature-branch` | **Date**: 2026-06-08 | **Spec**: `/specs/001-organizar-acervo-musical/spec.md`

**Input**: Feature specification from `/specs/001-organizar-acervo-musical/spec.md`

## Summary

Construir um sistema desktop cross-platform para importar colecoes MP3 em larga escala,
enriquecer metadados (incluindo covers), revisar manualmente, organizar arquivos em estrutura
deterministica com links por pais/cover, reproduzir acervo com hotkeys globais, registrar
historico de escuta e recomendar albuns com anti-repeticao. O design prioriza jobs retomaveis,
persistencia local e contratos claros para sincronizacao de catalogo online e regras de
filesystem.

## Technical Context

**Language/Version**: Java 21

**Primary Dependencies**: JavaFX (UI), JavaFX MediaPlayer (playback MP3), jaudiotagger (ID3),
SQLite JDBC (persistencia local), MusicBrainz WS client (metadados e covers), Discogs API client
(discografia e edicoes), Wikidata client (pais e normalizacao), AcoustID client (identificacao por
fingerprint), Cover Art Archive client (capas), biblioteca de hotkeys globais para JVM

**Storage**: SQLite local + filesystem local organizado

**Testing**: JUnit 5 + testes de integracao para DB/filesystem/sync + regressao de recomendacao

**Target Platform**: Windows 10+ e Linux desktop moderno (x86_64)

**Project Type**: desktop-app

**Java 21 Rationale**: Escolhido por ser LTS moderna, bem suportada por bibliotecas desktop e por
oferecer bom equilibrio entre estabilidade, recursos de linguagem e empacotamento multiplataforma.
Nao foi escolhido apenas por semelhanca com a implementacao anterior.

**Performance Goals**:
- Processar importacao em lotes com progresso continuo em acervo massivo
- Disponibilizar notificacao de albuns ausentes em ate 10 minutos apos ciclo de verificacao
- Responder play/pause por global key em ate 200ms durante reproducao ativa

**Constraints**:
- Nao modificar arquivos de origem
- Garantir compatibilidade de links simbolicos Windows/Linux
- Respeitar limite de taxa e termos de uso/licenca de cada provedor externo
- Operar com tolerancia a falhas de rede e retomada por checkpoint

**External Metadata Strategy**:
- Fonte canonica principal: MusicBrainz (identidade de artista/album/faixa e relacao cover-de)
- Fallback de catalogo: Discogs (detectar albuns/edicoes ausentes quando cobertura for parcial)
- Complemento de contexto: Wikidata (pais de origem e aliases adicionais)
- Recuperacao de arquivos pobres em tag: AcoustID (fingerprint para identificacao)
- Capa e artwork: Cover Art Archive
- Politica de reconciliacao: manter IDs externos por entidade e score de confianca por match

**Playback Strategy**:
- Engine primaria: JavaFX MediaPlayer para reproduzir MP3 com integracao simples com a UI
- Extensao futura recomendada: se surgirem requisitos de formatos adicionais ou maior controle de
  codec/playlist, avaliar VLCJ como engine alternativa sem alterar a camada de UI
- Hotkeys globais: biblioteca JVM dedicada separada do motor de playback

**Scale/Scope**:
- Biblioteca alvo de ate 1TB e centenas de milhares de faixas
- Fluxos MVP: importar/revisar/organizar + links + player + recomendacao + monitoramento online

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

### Gate Review (Pre-Design)
- Code Quality Gate: **PASS**
  - Definido padrao de camadas (domain/application/infrastructure/ui) e contratos para limites.
- Testing Gate: **PASS**
  - Estrategia explicita de testes unitarios, integracao e regressao para fluxos criticos.
- UX Consistency Gate: **PASS**
  - Fluxos unificados em quickstart para importacao, revisao, player e recomendacao.
- Performance Gate: **PASS**
  - Metas e limites definidos (SC-007, SC-008, SC-010) e refletidos em contratos.
- Traceability Gate: **PASS**
  - Decisoes de research mapeadas para FR-001..FR-020 e SC-001..SC-010.

### Gate Review (Post-Design)
- Code Quality Gate: **PASS** (data model e contratos explicitam regras e limites)
- Testing Gate: **PASS** (quickstart e plano de testes cobrem parse, IO, sync e recomendacao)
- UX Consistency Gate: **PASS** (cenarios e jornadas mantidos sem contradicoes)
- Performance Gate: **PASS** (job model e contract de sincronizacao com SLA definido)
- Traceability Gate: **PASS** (artefatos de fase 0/1 referenciam requisitos da spec)

## Project Structure

### Documentation (this feature)

```text
specs/001-organizar-acervo-musical/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
│   ├── catalog-sync-contract.md
│   ├── filesystem-layout-contract.md
│   └── player-and-recommendation-contract.md
└── tasks.md
```

### Source Code (repository root)

```text
src/
├── domain/
│   ├── model/
│   └── rules/
├── application/
│   ├── importer/
│   ├── organizer/
│   ├── sync/
│   ├── player/
│   └── recommendation/
├── infrastructure/
│   ├── db/
│   ├── filesystem/
│   ├── metadata/
│   └── scheduler/
└── ui/
    ├── importreview/
    ├── library/
    └── player/

tests/
├── unit/
├── integration/
└── contract/
```

**Structure Decision**: Projeto unico desktop com arquitetura em camadas para isolar regras de
dominio das integracoes de IO (filesystem, DB, webservice, hotkeys) e permitir testes de
regressao previsiveis.

## Complexity Tracking

> **Fill ONLY if Constitution Check has violations that must be justified**

Nenhuma violacao de constituicao identificada nesta fase.
