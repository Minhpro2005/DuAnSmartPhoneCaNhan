<template>
    <div class="filter-sidebar bg-light p-3 rounded shadow-sm">
      <h5 class="fw-bold mb-3"><i class="bi bi-funnel-fill me-2"></i> Bộ lọc tìm kiếm</h5>
  
      <div class="accordion" id="filterAccordion">
        <!-- Tên SP -->
        <div class="mb-3">
          <input v-model="filters.tenSP" type="text" class="form-control" placeholder="Tên sản phẩm..." />
        </div>
  
        <!-- Hãng -->
        <div class="accordion-item">
          <h6 class="accordion-header">
            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#hangCollapse">
              Hãng
            </button>
          </h6>
          <div id="hangCollapse" class="accordion-collapse collapse" data-bs-parent="#filterAccordion">
            <div class="accordion-body">
              <div class="form-check" v-for="hang in uniqueBrands" :key="hang">
                <input class="form-check-input" type="radio" :value="hang" v-model="filters.hang" :id="'hang-' + hang">
                <label class="form-check-label" :for="'hang-' + hang">{{ hang }}</label>
              </div>
              <div class="form-check">
                <input class="form-check-input" type="radio" value="" v-model="filters.hang" id="hang-all">
                <label class="form-check-label" for="hang-all">Tất cả</label>
              </div>
            </div>
          </div>
        </div>
  
        <!-- Giá -->
        <div class="accordion-item">
          <h6 class="accordion-header">
            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#giaCollapse">
              Giá bán
            </button>
          </h6>
          <div id="giaCollapse" class="accordion-collapse collapse" data-bs-parent="#filterAccordion">
            <div class="accordion-body">
              <div class="form-check" v-for="(label, value) in priceOptions" :key="value">
                <input class="form-check-input" type="radio" :value="value" v-model="filters.giaBan" :id="'gia-' + value">
                <label class="form-check-label" :for="'gia-' + value">{{ label }}</label>
              </div>
              <div class="form-check">
                <input class="form-check-input" type="radio" value="" v-model="filters.giaBan" id="gia-all">
                <label class="form-check-label" for="gia-all">Tất cả</label>
              </div>
            </div>
          </div>
        </div>
  
        <!-- Màu sắc -->
        <div class="accordion-item">
          <h6 class="accordion-header">
            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#mauCollapse">
              Màu sắc
            </button>
          </h6>
          <div id="mauCollapse" class="accordion-collapse collapse" data-bs-parent="#filterAccordion">
            <div class="accordion-body">
              <div class="form-check" v-for="mau in uniqueColors" :key="mau">
                <input class="form-check-input" type="checkbox" :value="mau" v-model="filters.mauSac" :id="'mau-' + mau">
                <label class="form-check-label" :for="'mau-' + mau">{{ mau }}</label>
              </div>
            </div>
          </div>
        </div>
      </div>
  
      <!-- Reset -->
      <div class="text-end mt-3">
        <button class="btn btn-outline-secondary btn-sm" @click="resetFilters">
          <i class="bi bi-arrow-clockwise me-1"></i> Đặt lại
        </button>
      </div>
    </div>
  </template>
  
  <script>
  
  export default {
    name: 'FilterSidebar',
    props: ['sanPhamList', 'filters'],
    emits: ['update:filters'],
    computed: {
      uniqueBrands() {
        return [...new Set(this.sanPhamList.map(sp => sp.hang))];
      },
      uniqueColors() {
        return [...new Set(this.sanPhamList.map(sp => sp.mauSac))];
      },
      priceOptions() {
        return {
          duoi2: "Dưới 2 triệu",
          '2to5': "2 - 5 triệu",
          '5to10': "5 - 10 triệu",
          tren10: "Trên 10 triệu"
        };
      }
    },
    methods: {
      resetFilters() {
        this.$emit('update:filters', {
          tenSP: '',
          hang: '',
          giaBan: '',
          mauSac: []
        });
      }
    }
  };
  </script>
  
  <style scoped>
  .accordion-button {
    font-size: 15px;
    font-weight: 500;
  }
  .form-check {
    margin-bottom: 8px;
  }
  </style>