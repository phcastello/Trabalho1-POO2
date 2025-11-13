<script setup lang="ts">
import { computed, ref, watch, onMounted, onBeforeUnmount, nextTick } from "vue";
import type { SearchableDropdownOption } from "./dropdown.types";

interface Props {
  id?: string;
  modelValue?: string | number | null;
  items?: SearchableDropdownOption[];
  placeholder?: string;
  disabled?: boolean;
  loading?: boolean;
  minSearchChars?: number;
  noResultsText?: string;
  emptyMessage?: string;
  clearable?: boolean;
  clearValue?: string | number | null;
  maxVisibleItems?: number;
}

const props = withDefaults(defineProps<Props>(), {
  modelValue: "",
  items: () => [],
  placeholder: "Digite para buscar...",
  disabled: false,
  loading: false,
  minSearchChars: 2,
  noResultsText: "Nenhum item encontrado.",
  emptyMessage: "Digite pelo menos {min} letras para buscar.",
  clearable: false,
  clearValue: "",
  maxVisibleItems: 7,
});

const emit = defineEmits<{
  (event: "update:modelValue", value: string | number | null): void;
  (event: "search", value: string): void;
}>();

let dropdownIdCounter = 0;
const instanceId = `searchable-dropdown-${++dropdownIdCounter}`;
const generatedId = computed(() => props.id ?? instanceId);
const listboxId = computed(() => `${generatedId.value}-listbox`);

const containerRef = ref<HTMLElement | null>(null);
const inputRef = ref<HTMLInputElement | null>(null);
const searchTerm = ref("");
const isTyping = ref(false);
const highlightedIndex = ref(-1);

const normalizedItems = computed(() => props.items ?? []);

function valueToken(value: string | number | null | undefined) {
  if (value === null || value === undefined) return "";
  return String(value);
}

function normalizeText(value: string | undefined | null) {
  if (!value) return "";
  return value
    .toLocaleLowerCase("pt-BR")
    .normalize("NFD")
    .replace(/[\u0300-\u036f]/g, "");
}

const selectedItem = computed(() =>
  normalizedItems.value.find((item) => valueToken(item.value) === valueToken(props.modelValue)) ?? null
);

const displayValue = computed(() => {
  if (isTyping.value) return searchTerm.value;
  return selectedItem.value?.label ?? "";
});

const normalizedQuery = computed(() => normalizeText(searchTerm.value));
const hasQuery = computed(() => normalizedQuery.value.length > 0);
const hasMinimumQuery = computed(() => normalizedQuery.value.length >= props.minSearchChars);

const filteredItems = computed(() => {
  if (!hasMinimumQuery.value) return [];
  const query = normalizedQuery.value;
  const filtered = normalizedItems.value.filter((item) => {
    const label = normalizeText(item.label);
    const description = normalizeText(item.description);
    const keywords = item.keywords?.map((keyword) => normalizeText(keyword)) ?? [];
    return (
      label.includes(query) ||
      (description.length > 0 && description.includes(query)) ||
      keywords.some((keyword) => keyword.includes(query))
    );
  });

  if (props.maxVisibleItems && props.maxVisibleItems > 0) {
    return filtered.slice(0, props.maxVisibleItems);
  }
  return filtered;
});

const isPanelVisible = computed(() => hasMinimumQuery.value);
const hasValue = computed(
  () =>
    valueToken(props.modelValue) !== valueToken(props.clearValue) &&
    valueToken(props.modelValue) !== "" &&
    props.modelValue !== null
);

watch(
  () => filteredItems.value.length,
  (length) => {
    if (length === 0) {
      highlightedIndex.value = -1;
    } else if (highlightedIndex.value === -1 || highlightedIndex.value >= length) {
      highlightedIndex.value = 0;
    }
  }
);

watch(
  () => props.modelValue,
  () => {
    if (!isTyping.value) {
      searchTerm.value = "";
    }
  }
);

function handleOutsideClick(event: MouseEvent) {
  const target = event.target as Node;
  if (!containerRef.value) return;
  if (!containerRef.value.contains(target)) {
    closeDropdown();
  }
}

onMounted(() => {
  document.addEventListener("mousedown", handleOutsideClick);
});

onBeforeUnmount(() => {
  document.removeEventListener("mousedown", handleOutsideClick);
});

function closeDropdown() {
  isTyping.value = false;
  searchTerm.value = "";
  highlightedIndex.value = -1;
}

function handleFocus(event: FocusEvent) {
  if (props.disabled) return;
  const target = event.target as HTMLInputElement;
  nextTick(() => {
    if (selectedItem.value) {
      target.select();
    }
  });
}

function handleBlur() {
  if (props.disabled) return;
  window.setTimeout(() => {
    closeDropdown();
  }, 80);
}

function handleInput(event: Event) {
  if (props.disabled) return;
  const target = event.target as HTMLInputElement;
  searchTerm.value = target.value;
  isTyping.value = true;
  emit("search", target.value);
}

function moveHighlight(direction: 1 | -1) {
  if (filteredItems.value.length === 0) return;
  const nextIndex = highlightedIndex.value + direction;
  if (nextIndex < 0) {
    highlightedIndex.value = filteredItems.value.length - 1;
  } else if (nextIndex >= filteredItems.value.length) {
    highlightedIndex.value = 0;
  } else {
    highlightedIndex.value = nextIndex;
  }
}

function handleKeydown(event: KeyboardEvent) {
  if (props.disabled) return;
  if (event.key === "ArrowDown") {
    if (!hasMinimumQuery.value) return;
    event.preventDefault();
    moveHighlight(1);
  } else if (event.key === "ArrowUp") {
    if (!hasMinimumQuery.value) return;
    event.preventDefault();
    moveHighlight(-1);
  } else if (event.key === "Enter") {
    if (!hasMinimumQuery.value) return;
    event.preventDefault();
    const current =
      highlightedIndex.value >= 0 ? filteredItems.value[highlightedIndex.value] : filteredItems.value[0];
    if (current) {
      selectItem(current);
    }
  } else if (event.key === "Escape") {
    closeDropdown();
  }
}

function selectItem(item: SearchableDropdownOption) {
  emit("update:modelValue", item.value);
  closeDropdown();
}

function clearSelection() {
  if (props.disabled) return;
  emit("update:modelValue", props.clearValue);
  closeDropdown();
  nextTick(() => {
    inputRef.value?.focus();
  });
}

function optionId(item: SearchableDropdownOption) {
  return `${generatedId.value}-option-${valueToken(item.value)}`;
}
</script>

<template>
  <div
    ref="containerRef"
    class="searchable-dropdown"
    :class="{ 'is-disabled': props.disabled, 'has-value': hasValue, 'is-active': hasMinimumQuery }"
  >
    <div class="dropdown-control">
      <input
        ref="inputRef"
        :id="generatedId"
        type="text"
        :value="displayValue"
        :placeholder="props.placeholder"
        :disabled="props.disabled"
        autocomplete="off"
        role="combobox"
        aria-autocomplete="list"
        :aria-expanded="isPanelVisible"
        :aria-controls="isPanelVisible ? listboxId : undefined"
        :aria-activedescendant="highlightedIndex >= 0 && filteredItems[highlightedIndex] ? optionId(filteredItems[highlightedIndex]) : undefined"
        @focus="handleFocus"
        @blur="handleBlur"
        @input="handleInput"
        @keydown="handleKeydown"
      />
      <button
        v-if="props.clearable && hasValue && !props.disabled"
        type="button"
        class="dropdown-clear"
        @mousedown.prevent
        @click.prevent="clearSelection"
        aria-label="Limpar seleção"
      >
        ×
      </button>
      <span class="dropdown-chevron" aria-hidden="true"></span>
    </div>

    <p v-if="isTyping && hasQuery && !hasMinimumQuery" class="dropdown-hint">
      {{ props.emptyMessage.replace("{min}", String(props.minSearchChars)) }}
    </p>

    <div v-if="isPanelVisible" class="dropdown-panel">
      <div v-if="props.loading" class="dropdown-message">Carregando opções...</div>
      <template v-else>
        <div v-if="filteredItems.length === 0" class="dropdown-message">
          {{ props.noResultsText }}
        </div>
        <ul v-else :id="listboxId" role="listbox" class="dropdown-list">
          <li
            v-for="(item, index) in filteredItems"
            :id="optionId(item)"
            :key="valueToken(item.value)"
            role="option"
            class="dropdown-item"
            :class="{ 'is-highlighted': index === highlightedIndex }"
            @mousedown.prevent="selectItem(item)"
          >
            <span class="item-label">{{ item.label }}</span>
            <span v-if="item.description" class="item-description">{{ item.description }}</span>
          </li>
        </ul>
      </template>
    </div>
  </div>
</template>

<style scoped>
.searchable-dropdown {
  position: relative;
  width: 100%;
}

.dropdown-control {
  position: relative;
  display: flex;
  align-items: center;
}

.dropdown-control input {
  padding-right: 2.75rem;
}

.dropdown-clear {
  position: absolute;
  right: 2rem;
  background: transparent;
  border: none;
  color: var(--text-muted);
  cursor: pointer;
  font-size: 1.1rem;
  line-height: 1;
  padding: 0;
  width: 1.2rem;
  height: 1.2rem;
  display: grid;
  place-items: center;
}

.dropdown-clear:hover {
  color: var(--text-primary);
}

.dropdown-chevron {
  position: absolute;
  right: 0.75rem;
  width: 0.85rem;
  height: 0.85rem;
  border-right: 2px solid var(--text-muted);
  border-bottom: 2px solid var(--text-muted);
  transform: rotate(45deg);
  pointer-events: none;
  transition: transform 0.2s ease;
}

.is-active .dropdown-chevron {
  transform: rotate(225deg) translateY(-2px);
}

.dropdown-panel {
  position: absolute;
  top: calc(100% + 0.4rem);
  left: 0;
  width: 100%;
  background: var(--surface, #fff);
  border-radius: var(--radius-md);
  border: 1px solid rgba(148, 163, 184, 0.5);
  box-shadow: var(--shadow-md);
  z-index: 20;
  max-height: 260px;
  overflow-y: auto;
}

.dropdown-message {
  padding: 0.9rem 1rem;
  font-size: 0.9rem;
  color: var(--text-muted);
}

.dropdown-list {
  list-style: none;
  margin: 0;
  padding: 0.4rem 0;
}

.dropdown-item {
  padding: 0.55rem 1rem;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
}

.dropdown-item:hover,
.dropdown-item.is-highlighted {
  background: rgba(59, 130, 246, 0.12);
}

.item-label {
  font-weight: 600;
  color: var(--text-primary);
}

.item-description {
  font-size: 0.85rem;
  color: var(--text-muted);
}

.dropdown-hint {
  margin: 0.4rem 0 0;
  font-size: 0.8rem;
  color: var(--text-muted);
}

.searchable-dropdown.is-disabled input {
  background: rgba(226, 232, 240, 0.5);
  cursor: not-allowed;
}
</style>
