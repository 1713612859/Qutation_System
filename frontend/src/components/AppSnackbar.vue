<template>
  <v-snackbar
      v-model="$snackbar.state.show"
      :color="$snackbar.state.color"
      :timeout="$snackbar.state.timeout"
      :location="$snackbar.state.location"
  >
    {{ $snackbar.state.message }}
    <template #actions>
      <v-btn variant="text" @click="$snackbar.close()">Close</v-btn>
    </template>
  </v-snackbar>
</template>

<script setup>
// 这里不需要导入，$snackbar 是全局注入的
</script>
