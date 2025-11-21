export interface UserSettings {
  uiLanguage: Language;
  notificationsEnabled: boolean;
  theme: Theme;
}

export enum Language {
  EN = 'EN',
  DE = 'DE'
}

export enum Theme {
  LIGHT = 'LIGHT',
  DARK = 'DARK',
  AUTO = 'AUTO'
}
