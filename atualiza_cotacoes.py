import requests
import csv
import json
from io import StringIO
from datetime import datetime

URL_CSV = "https://docs.google.com/spreadsheets/d/1Zyzbrjd7mAFDaEKaXURGzA0o0cDA4p35MCcDW-2mwo8/export?format=csv&gid=1706485275"
JSON_FILE = "cotacoes.json"

print("🔄 Baixando dados mais recentes...")
response = requests.get(URL_CSV)
response.raise_for_status()  

csv_content = StringIO(response.text)
reader = csv.DictReader(csv_content)
dados = []

for linha in reader:
    
    linha["atualizado_em"] = datetime.now().strftime("%d/%m/%Y %H:%M:%S")
    dados.append(linha)


with open(JSON_FILE, "w", encoding="utf-8") as f:
    json.dump(dados, f, indent=2, ensure_ascii=False)

print(f"✅ Pronto! {len(dados)} ativos salvos em '{JSON_FILE}'")