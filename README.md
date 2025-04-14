<div align="center">
  <h1>🔒 Sistema de autenticação com suporte a refresh tokens. 🔓</h1>
  <p align="center">Oferece amazenamento seguro em HttpOnly Cookie e controle de acesso baseado em roles.</p>
  
  [Sobre](#sobre) • 
  [Aprendizados](#aprendizados) • 
  [Tecnologias](#tecnologias) • 
  [Como rodar](#run)
</div>

📎 Sobre.
=========

Projeto desenvolvido com o objetivo de entender como funciona e como é a implementação de um sistema de autenticação de usuário com controle de acesso baseado em roles. Com o auxilio do spring security configuramos 
um filtro para validar o access token JWT (a cada requisição esse filtro é aplicado) e injetar o usuário logado no contexto do spring security, permitindo que acessemos o usuário logado em qualquer service 
da aplicação.

<h3>End points</h3>

<strong>Signin</strong> - ```/auth/signin``` <br>
End point responsável por criar o access token. Ele também faz a criação do refresh token, e salva no banco. Caso o usuário faça login novamente o token é buscado no banco, validado e retornado, sendo um refresh token
válido por usuário.
Por questão de segurança, o refresh token é opaco e contém apenas o identificador único do usuário, o email.
```
{
  "email": "email@gmail.com",
  "password": "email"
}
```
O cliente recebe como resposta o access e refresh token salvos no HttpOnly Cookies. Logo, para todo recurso que o cliente va acessar os cookies são enviados e validados, liberando ou não o acesso.

<strong>Refresh Token</strong> - ```/auth/refreshToken``` <br>
Esse é um end point público pois o access token do usuário já expirou. Porém, ele acessa o refresh token no HttpOnly Cookies, faz a validação do mesmo com o refresh token salvo no banco e por fim retorna um novo
access token.

E como esse end point é acionado? O cliente tenta acessar um recurso que ele tem permissão, porém o access token expirou. O back end retorna um erro com o status 401 Unauthorized, o cliente verifica o código
e de forma automática chama esse end point para gerar um novo access token.

<strong>Signout</strong> - ```/auth/signout``` <br>
Caso o usuário saia da sua conta, esse end point deve ser chamado. Ele apaga o refresh token do banco e limpa o HttpOnly Cookies.

<strong>Signup</strong> - ```/auth/signup``` <br>
```
{
  "username": "Email",
  "email": "email@gmail.com",
  "password": "email"
}
```

<strong>Obs: O projeto tem outros end point para exemplificar como é o controle de acesso baseado em roles.</strong> <br>
<strong>Obs²: Como referência recomendo: <a href="https://www.bezkoder.com/spring-boot-security-login-jwt/">Spring Security Refresh Token with JWT in Spring Boot</a></strong>

📚 Aprendizados.
================

*  Funcionamento e estrutura do Spring Security (AuthenticationManager, UsernamePasswordAuthenticationToken, UserDetails, UserDetailsService)
*  Ainda sobre o Spring Security, destaco a configuração de um filtro do tipo OncePerRequestFilter.
*  Criação de tokens JWT assinados usando algoritmos simétricos (Chave única) e assimétricos (Chave pública e privada)
*  Utilização de HttpOnly Cookies.
*  Configuração do CORS.

🛠 Tecnologias.
===============

[![JAVA](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/pt-BR/) [![Spring boot](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot) [![PostgreSQL](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)

ℹ️ Como rodar.
===============
1. Em um diretório, clone o projeto:
```
git clone https://github.com/c-souzs/refresh-token-jwt.git
```
2. Configure um banco postgres.
3. Adicione as variaveis de ambiente na sua IDE conforme o arquivo application.properties.
4. No arquivo application.properties também é possível configurar informações do JWT e do CORS.
