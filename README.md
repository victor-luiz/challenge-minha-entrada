# Desafio de Estágio - Minha Entrada

## 📝 Descrição

Um projeto Android desenvolvido em Kotlin para um desafio técnico de estágio. O aplicativo, é uma plataforma de gerenciamento de usuários e eventos, onde os usuários podem se cadastrar, logar, gerenciar seus perfis e criar/visualizar/editar/excluir seus próprios eventos.

O projeto foi desenvolvido com foco em arquitetura, boas práticas de código, uma experiência de usuário intuitiva, indo muito além dos requisitos básicos do desafio.

[![](https://img.shields.io/badge/Baixar_APK-v1.0.0-brightgreen)](https://github.com/victor-luiz/challenge-minha-entrada/releases/download/v1.0.0/app-release-unsigned.apk)

---

## ✨ Funcionalidades

- [x] **Arquitetura:**
  - [x] Arquitetura MVVM (Model-View-ViewModel).
  - [x] Injeção de Dependência com Hilt.
- [x] **Autenticação de Usuário:**
  - [x] Telas de Cadastro e Login.
  - [x] Senhas armazenadas de forma segura usando Hashing (SHA-256) com Salt.
- [x] **Gerenciamento de Sessão:**
  - [x] Sessão de usuário persistente via `SharedPreferences` (gerenciada por um `SessionManager`).
  - [x] Splash Screen animada com navegação inteligente (direciona para Login ou Tela Principal dependendo do estado da sessão).
- [x] **CRUD Completo de Perfil de Usuário:**
  - [x] Edição de dados do perfil.
  - [x] Exclusão de conta com diálogo de confirmação.
- [x] **CRUD Completo de Eventos:**
  - [x] Criação e Edição de eventos através de uma modal reutilizável.
  - [x] Listagem reativa (`Flow` + `LiveData`) de eventos criados pelo usuário.
  - [x] Visualização dos detalhes de um evento em uma tela dedicada.
  - [x] Exclusão de evento com diálogo de confirmação.
- [x] **UI/UX:**
  - [x] Customizado com Material Design 3 (cores e fontes), com suporte completo a **Modo Escuro (Dark Mode)**.
  - [x] Componentes reutilizáveis (`DatePicker`, `LocationSelector`, `CategoryChip`, `UserInfoHeader`).
  - [x] Layouts responsivos que se adaptam a diferentes tamanhos de tela (usando `ScrollView`, `ConstraintLayout`).
- [x] **Testes Automatizados:**
  - [x] Suíte de Testes de Unidade com JUnit e MockK para todas as ViewModels, garantindo a qualidade da lógica de negócio.

---

## 🛠️ Tecnologias e Bibliotecas

- **Linguagem:** 100% [Kotlin](https://kotlinlang.org/)
- **Arquitetura:** MVVM
- **Jetpack:**
  - [Hilt](https://developer.android.com/training/dependency-injection/hilt-android) (Injeção de Dependência)
  - [Room](https://developer.android.com/training/data-storage/room) (Banco de Dados SQLite com Migrações)
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel)
  - [LiveData](https://developer.android.com/topic/libraries/architecture/livedata)
  - [Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) + `Flow` (Assincronismo e Reatividade)
- **UI:**
  - Android Views com [ViewBinding](https://developer.android.com/topic/libraries/view-binding)
  - [Material Design 3](https://m3.material.io/)
- **Testes:**
  - [JUnit](https://junit.org/junit5/)
  - [MockK](https://mockk.io/)

---

## 🚀 Como Rodar o Projeto

1.  Clone este repositório.
2.  Abra o projeto no Android Studio ou IntelliJ IDEA.
3.  Deixe o Gradle sincronizar e baixar todas as dependências.
4.  Rode o aplicativo em um emulador ou dispositivo físico (API 26+).
