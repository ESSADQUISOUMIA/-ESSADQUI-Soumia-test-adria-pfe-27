import argparse
import json
import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.compose import ColumnTransformer
from sklearn.pipeline import Pipeline
from sklearn.impute import SimpleImputer
from sklearn.preprocessing import OneHotEncoder, StandardScaler
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import classification_report, confusion_matrix, average_precision_score

def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('csv')
    args = parser.parse_args()
    df = pd.read_csv(args.csv)
    numeric = ['amount', 'hour', 'transactions_last_24h', 'average_amount_last_7d']
    categories = ['country', 'merchant_category']
    if df['is_fraud'].isna().any() or not set(df['is_fraud']).issubset({0,1}):
        raise ValueError('is_fraud doit contenir uniquement 0 et 1')
    print('Proportion de fraudes:', df.is_fraud.mean())
    print('Valeurs manquantes:', df.isna().sum().to_dict())
    X_train, X_test, y_train, y_test = train_test_split(df[numeric + categories], df.is_fraud, test_size=.2, stratify=df.is_fraud, random_state=42)
    prep = ColumnTransformer([
        ('num', Pipeline([('impute', SimpleImputer(strategy='median')), ('scale', StandardScaler())]), numeric),
        ('cat', Pipeline([('impute', SimpleImputer(strategy='most_frequent')), ('encode', OneHotEncoder(handle_unknown='ignore'))]), categories)])
    model = Pipeline([('preprocessing', prep), ('classifier', LogisticRegression(class_weight='balanced', max_iter=2000, random_state=42))])
    model.fit(X_train, y_train)
    prediction = model.predict(X_test)
    probability = model.predict_proba(X_test)[:, 1]
    print(classification_report(y_test, prediction, zero_division=0))
    print('Matrice de confusion:', confusion_matrix(y_test, prediction, labels=[0,1]))
    print('Average precision:', average_precision_score(y_test, probability))

if __name__ == '__main__':
    main()
