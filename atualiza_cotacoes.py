import requests
import csv
import json
import os
from io import StringIO
from datetime import datetime

URL_CSV = "https://docs.google.com/spreadsheets/d/1Zyzbrjd7mAFDaEKaXURGzA0o0cDA4p35MCcDW-2mwo8/export?format=csv&gid=1706485275"

# Obtém o diretório do script
SCRIPT_DIR = os.path.dirname(os.path.abspath(__file__))
JSON_FILE_RESOURCES = os.path.join(SCRIPT_DIR, "src", "main", "resources", "data", "cotacoes.json")

print("🔄 Baixando dados mais recentes...")
response = requests.get(URL_CSV)
response.raise_for_status()  

csv_content = StringIO(response.text)
reader = csv.DictReader(csv_content)
dados = []

for linha in reader:
    linha["atualizado_em"] = datetime.now().strftime("%d/%m/%Y %H:%M:%S")
    dados.append(linha)

# Salva em src/main/resources/data/ (onde o Spring Boot lê)
os.makedirs(os.path.dirname(JSON_FILE_RESOURCES), exist_ok=True)
with open(JSON_FILE_RESOURCES, "w", encoding="utf-8") as f:
    json.dump(dados, f, indent=2, ensure_ascii=False)
print(f"✅ Arquivo salvo em '{JSON_FILE_RESOURCES}'")

print(f"✅ Pronto! {len(dados)} ativos atualizados")