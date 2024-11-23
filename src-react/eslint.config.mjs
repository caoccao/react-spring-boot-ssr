import { fixupConfigRules, fixupPluginRules } from '@eslint/compat'
import { FlatCompat } from '@eslint/eslintrc'
import js from '@eslint/js'
import typescriptEslint from '@typescript-eslint/eslint-plugin'
import tsParser from '@typescript-eslint/parser'
import _import from 'eslint-plugin-import'
import jsxA11Y from 'eslint-plugin-jsx-a11y'
import prettier from 'eslint-plugin-prettier'
import react from 'eslint-plugin-react'
import reactHooks from 'eslint-plugin-react-hooks'
import reactRefresh from 'eslint-plugin-react-refresh'
import globals from 'globals'
import path from 'node:path'
import { fileURLToPath } from 'node:url'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)
const compat = new FlatCompat({
  baseDirectory: __dirname,
  recommendedConfig: js.configs.recommended,
  allConfig: js.configs.all,
})

export default [
  {
    ignores: [
      '**/dist',
      '**/.eslintrc.cjs',
      '**/node_modules',
      '**/dist',
      '**/coverage',
      '**/.eslintcache',
      '**/.eslintrc.cjs',
      '**/mockServiceWorker.js',
    ],
  },
  ...fixupConfigRules(
    compat.extends(
      'eslint:recommended',
      'plugin:import/typescript',
      'plugin:react/recommended',
      'plugin:react-hooks/recommended',
      'plugin:jsx-a11y/strict',
      'plugin:react/jsx-runtime',
      'plugin:@typescript-eslint/recommended',
      'plugin:@typescript-eslint/stylistic',
      'plugin:prettier/recommended',
      'prettier',
    ),
  ),
  {
    plugins: {
      react: fixupPluginRules(react),
      '@typescript-eslint': fixupPluginRules(typescriptEslint),
      import: fixupPluginRules(_import),
      'jsx-a11y': fixupPluginRules(jsxA11Y),
      'react-hooks': fixupPluginRules(reactHooks),
      'react-refresh': reactRefresh,
      prettier: fixupPluginRules(prettier),
    },

    languageOptions: {
      globals: {
        ...globals.browser,
      },

      parser: tsParser,
      ecmaVersion: 'latest',
      sourceType: 'module',

      parserOptions: {
        ecmaFeatures: {
          jsx: true,
        },

        project: ['./tsconfig.json', './tsconfig.node.json'],
        tsconfigRootDir: './',
      },
    },

    settings: {
      react: {
        version: 'detect',
      },
    },

    rules: {
      'no-var': 'error',
      'no-alert': 'error',
      'no-console': 'error',
      'prefer-const': 'error',
      'import/no-duplicates': 'error',
      'import/no-self-import': 'error',
      'import/no-relative-packages': 'error',
      'import/no-relative-parent-imports': 'error',
      'import/consistent-type-specifier-style': ['error', 'prefer-inline'],
      'import/no-empty-named-blocks': 'error',
      'import/no-extraneous-dependencies': 'error',
      'import/no-import-module-exports': 'error',
      'import/newline-after-import': 'error',
      'import/group-exports': 'error',
      'import/exports-last': 'error',

      'import/no-useless-path-segments': [
        'error',
        {
          noUselessIndex: true,
        },
      ],

      '@typescript-eslint/no-explicit-any': 'error',
      '@typescript-eslint/no-non-null-assertion': 'error',
      '@typescript-eslint/no-unused-vars': 'error',
      '@typescript-eslint/consistent-type-definitions': 'off',
      'react-hooks/rules-of-hooks': 'error',
      'react-hooks/exhaustive-deps': 'error',

      'prettier/prettier': [
        'warn',
        {
          endOfLine: 'lf',
          singleQuote: true,
        },
      ],

      'react-refresh/only-export-components': [
        'error',
        {
          allowConstantExport: true,
        },
      ],
    },
  },
]
