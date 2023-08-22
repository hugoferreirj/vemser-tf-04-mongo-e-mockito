-- Criar DB
use wb_health

-- Criar colecction
db.createCollection("pacientes")

-- Inserir
db.pacientes.insertMany([
{
    "_id": 1,
    "nome": "Hugo Ferreira",
    "cep": "13560049",
    "data_nascimento": new Date(2002,05,09),
    "cpf": "40379144840",
    "salario_mensal": 800,
    "email": "hugo.vieira@gmail.com"
},
{
    "_id": 2,
    "nome": "Livia Fausto",
    "cep": "14571020",
    "data_nascimento": new Date(1995, 10, 22),
    "cpf": "12345678901",
    "salario_mensal": 3500,
    "email": "liva@outlook.com"
},
{
    "_id": 3,
    "nome": "Deyvison Silva",
    "cep": "80010010",
    "data_nascimento": new Date(1988, 3, 15),
    "cpf": "98765432109",
    "salario_mensal": 4500,
    "email": "deyv@gmail.com"
},
{
    "_id": 4,
    "nome": "Mayra Santos",
    "cep": "60160160",
    "data_nascimento": new Date(1990, 7, 5),
    "cpf": "24681357900",
    "salario_mensal": 2200,
    "email": "mayra.santos@outlook.com"
},
{
     "_id": 5,
     "nome": "Rafael Santos",
     "cep": "23045080",
     "data_nascimento": new Date(1985, 1, 30),
     "cpf": "13579246810",
     "salario_mensal": 6000,
     "email": "rafaellazzari@gmail.com"
 }
])

-- Buscas
db.pacientes.find({"email": /@gmail.com/})

db.pacientes.find({"salario_mensal": { $gt: 3000}})

db.pacientes.find({"nome": /Santos/})

db.pacientes.find({
  $expr: {
    $eq: [{ $year: "$data_nascimento" }, 2002]
  }
})