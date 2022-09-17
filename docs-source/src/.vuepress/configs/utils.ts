export const i18n = {
    space: ' ',
    string: (content: string, locale: string) => {
        return '/' + locale + content;
    },
    array: (contents: string[], locale: string) => {
        const newContents: string[] = [];
        contents.forEach((content) => {
            newContents.push(i18n.string(content, locale));
        });
        return newContents;
    }
};