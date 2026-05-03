---
name: conventional-commits
description: user para criação de commits padronizados usando Conventional Commits (https://www.conventionalcommits.org/pt-br/v1.0.0/)
---

# Diretrizes para Commits Padronizados (Conventional Commits)

Esta skill define as regras e boas práticas para a criação de mensagens de commit neste repositório. O objetivo é manter um histórico limpo, compreensível e que facilite a geração de changelogs e o versionamento semântico (SemVer).

## Estrutura da Mensagem de Commit

A mensagem de commit deve ser estruturada da seguinte forma:

```
<tipo>[escopo opcional]: <descrição curta no imperativo>

[corpo opcional, separado da descrição por uma linha em branco]

[rodapé(s) opcional(is), separado do corpo por uma linha em branco]
```

## Tipos Permitidos (`<tipo>`)

Os tipos devem estar sempre em minúsculo. Os tipos principais são:

*   **`feat`**: Adiciona uma nova funcionalidade (feature) à base de código. Correlaciona-se com MINOR no Semantic Versioning.
*   **`fix`**: Resolve um bug ou problema na base de código. Correlaciona-se com PATCH no Semantic Versioning.

Outros tipos comuns suportados (baseados na convenção do Angular):

*   **`build`**: Alterações que afetam o sistema de build ou dependências externas (ex: maven, npm).
*   **`chore`**: Atualizações de tarefas, scripts ou configuração que não afetam o código fonte de produção ou testes.
*   **`ci`**: Alterações nos arquivos e scripts de configuração de CI (Integração Contínua) (ex: GitHub Actions, Travis).
*   **`docs`**: Alterações apenas na documentação (ex: README, JavaDoc, comentários).
*   **`style`**: Alterações que não afetam o significado do código (espaçamento, formatação, ponto e vírgula, etc).
*   **`refactor`**: Uma alteração de código que não corrige um bug nem adiciona uma nova funcionalidade.
*   **`perf`**: Uma alteração de código que melhora a performance.
*   **`test`**: Adição de testes ausentes ou correção de testes existentes.

## Escopo (`[escopo]`)

*   **Opcional**, mas recomendado se ajudar a contextualizar a mudança.
*   Deve consistir em um substantivo que descreve a seção da base de código afetada.
*   Deve ser cercado por parênteses.
*   Exemplos: `feat(api):`, `fix(auth):`, `refactor(database):`.

## Descrição (`<descrição>`)

*   Deve ser um resumo curto (idealmente até 50-72 caracteres) das mudanças.
*   Deve ser escrito no tempo imperativo, presente (ex: "adiciona", "corrige", "muda" - ou em inglês "add", "fix", "change"). Não use o passado ("adicionado", "corrigido").
*   Não capitalize a primeira letra (a menos que seja uma palavra que exija maiúscula).
*   Não coloque ponto final (`.`) no final da linha.
*   Não utilize emojis.
*   Escreva em Português-BR

## Corpo (`[corpo]`)

*   **Opcional**. Use se precisar explicar o *o quê* e o *porquê* das mudanças, não o *como*.
*   Deve ser separado da descrição curta por uma linha em branco.
*   Pode ter múltiplas quebras de linha e parágrafos.
*   Escreva em Português-BR

## Rodapé (`[rodapé(s)]`)

*   **Opcional**. Usado principalmente para referenciar IDs de Issues/Tickets rastreados (ex: `Fixes #123`, `Closes #456`) ou para indicar Breaking Changes (mudanças de quebra).

## Mudanças de Quebra (Breaking Changes)

*   Seja no tipo/escopo ou no rodapé, uma mudança de quebra deve ser indicada claramente.
*   **No tipo/escopo**: Adicione um `!` logo antes dos dois-pontos. Exemplo: `feat(api)!: altera a resposta do endpoint de login`.
*   **No rodapé**: Inicie o rodapé com `BREAKING CHANGE:` seguido por um espaço ou duas linhas em branco.

## Exemplos de Mensagens

**Commit com tipo e descrição (mais comum):**
```
feat: adiciona serviço de autenticação com JWT
```

**Commit com escopo:**
```
fix(controller): corrige erro de NPE na criação de usuário
```

**Commit com corpo explicando o "porquê":**
```
refactor(infra): substitui usocase por controller no teste

Isso foi necessário porque a injeção do usecase com @InjectMock 
estava falhando devido a problemas de escopo CDI no Quarkus.
```

**Commit com Breaking Change (usando `!`):**
```
feat(api)!: remove campo obsoleto 'telefone' da criação de usuário
```

**Commit com rodapé para fechar issue:**
```
fix: resolve vazamento de conexão no banco

Fixes #42
```

## Regras de Execução da Skill (Para o Agente)

1.  Quando for solicitado que você faça um commit, analise cuidadosamente as modificações realizadas no repositório (`git status`, `git diff`).
2.  Determine o **Tipo** correto (`feat`, `fix`, `chore`, etc.) que melhor encapsula todas as mudanças. Se as mudanças forem de categorias diferentes, agrupe-as ou prefira a de maior impacto (ex: `feat` sobrepõe `test`).
3.  Defina o **Escopo** se as mudanças estiverem limitadas a um contexto bem delimitado.
4.  Crie uma **Descrição** concisa e no imperativo.
5.  Adicione um **Corpo** se houver contexto importante (motivações técnicas) que justifique as alterações.
6.  Execute o comando git respectivo para consolidar o commit: `git commit -m "tipo(escopo): descrição" -m "corpo"`
