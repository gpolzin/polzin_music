# Quickstart - Organizador e Player de Acervo Musical

## 1. Pre-requisitos
- Java 21+
- Maven 3.9+
- Acesso de leitura ao acervo origem e escrita ao destino organizado
- Conectividade com MusicBrainz (para enriquecimento e verificação de albuns ausentes)

## 2. Setup inicial
```bash
mvn clean verify
```

## 3. Configuracao local
1. Definir pasta origem para importacao.
2. Definir pasta destino organizada.
3. Validar permissao para criacao de links simbolicos no sistema operacional.

## 4. Fluxo MVP (US1)
1. Abrir tela de importacao.
2. Selecionar pasta com MP3.
3. Executar leitura de metadados com fallback (ID3v2 -> ID3v1 -> nome de arquivo/pasta).
4. Resolver duplicatas musica por musica quando detectadas, comparando tamanho e qualidade.
5. Revisar cada faixa e confirmar dados.
6. Finalizar e validar estrutura de saida:
   - Artistas/<nome>/<tipo_album>/<ano - album>
   - Faixas no formato NN - Titulo.extensao

## 5. Fluxo de navegacao por links (US2)
1. Confirmar criacao de links por pais em Paises/<pais>/<link artista>.
2. Confirmar criacao de links por estilo em Estilos/<style_name>/<artist_link>.
3. Confirmar criacao de links por marcacoes em Marcacoes/<nome_marcacao>/<music_link>.
4. Confirmar links de cover para artista original quando relacao existir.

## 6. Fluxo player e historico (US3)
1. Reproduzir album no player.
2. Acionar play/pause por global key.
3. Validar persistencia de ultima escuta por faixa e album.

## 7. Fluxo de recomendacao e monitoramento online (US4)
1. Aplicar tags customizadas em faixas e nota em albuns.
2. Solicitar recomendacao e validar penalizacao de repeticao recente.
3. Executar verificacao periodica online por artista.
4. Validar notificacao de albuns ausentes no acervo local.

## 8. Testes
```bash
mvn test
```

## 9. Qualidade
```bash
mvn -q -DskipTests=false verify
```

## 10. Definicao de pronto
- Gates da constituicao aprovados (qualidade, testes, UX, performance, rastreabilidade)
- Criterios de sucesso SC-001..SC-014 cobertos por evidencia de teste
